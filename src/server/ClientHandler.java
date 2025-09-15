package server;

import common.*;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO trxDAO = new TransactionDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final MatchDAO matchDAO = new MatchDAO();
    private final RoomManager rooms;

    private User me; // người chơi đã đăng nhập
    private RoomManager.ActiveRoom current;

    public ClientHandler(Socket s, RoomManager rooms) { this.socket = s; this.rooms = rooms; }

    @Override public void run() {
        try (socket) {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in  = new ObjectInputStream(socket.getInputStream());

            // Gửi danh sách phòng ngay khi client kết nối
            sendRooms();

            while (true) {
                Message m = (Message) in.readObject();
                switch (m.type) {
                    case REGISTER   -> onRegister(m);
                    case LOGIN      -> onLogin(m);
                    case JOIN_ROOM  -> onJoinRoom(m);
                    case PLAY_MOVE  -> onPlayMove(m);
                    case RECHARGE   -> onRecharge(m);
                    case GET_RANKING-> onGetRanking();
                    case INFO       -> sendRooms();       // nút "Làm mới"
                    case LOGOUT     -> { return; }
                    default         -> send(new Message(MessageType.ERROR).put("msg", "Unknown: " + m.type));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // để bạn thấy lỗi trên Console
        } finally {
            if (current != null) rooms.leaveRoom(this);
        }
    }

    private void sendRooms() throws SQLException, IOException {
        List<Room> list = rooms.snapshot();
        if (list.isEmpty()) {
            // Nếu DB chưa có phòng, tự tạo mặc định
            roomDAO.ensureDefaultRooms();
            rooms.loadRoomsFromDB(roomDAO.listAll());
            list = rooms.snapshot();
        }
        send(new Message(MessageType.ROOM_LIST).put("rooms", list));
    }

    public void send(Message msg) throws IOException { out.writeObject(msg); out.flush(); }

    private void onRegister(Message m) throws IOException {
        String u = m.get("username"); String pHash = m.get("password_hash");
        try {
            if (userDAO.findByUsername(u) != null) { send(new Message(MessageType.LOGIN_FAIL).put("msg", "Username exists")); return; }
            me = userDAO.create(u, pHash);
            send(new Message(MessageType.LOGIN_OK).put("user", me));
        } catch (SQLException e) { send(new Message(MessageType.ERROR).put("msg", e.getMessage())); }
    }

    private void onLogin(Message m) throws IOException {
        String u = m.get("username"); String pHash = m.get("password_hash");
        try {
            if (userDAO.checkLogin(u, pHash)) {
                me = userDAO.findByUsername(u);
                send(new Message(MessageType.LOGIN_OK).put("user", me));
                sendRooms();
            } else send(new Message(MessageType.LOGIN_FAIL).put("msg", "Invalid credentials"));
        } catch (SQLException e) { send(new Message(MessageType.ERROR).put("msg", e.getMessage())); }
    }

    private void onJoinRoom(Message m) throws IOException {
        if (me == null) { send(new Message(MessageType.ERROR).put("msg", "Login first")); return; }
        int roomId = (Integer) m.get("room_id");
        if (!rooms.joinRoom(roomId, this)) { send(new Message(MessageType.ROOM_FULL)); return; }
        current = rooms.get(roomId);
        send(new Message(MessageType.ROOM_JOINED).put("room", current.room));
        if (rooms.bothSeated(current)) {
            try {
                current.currentMatchId = matchDAO.create(
                        current.room.roomId,
                        current.p1.handler.me.id,
                        current.p2.handler.me.id,
                        current.room.betAmount);
            } catch (Exception ignored) { }
            rooms.resetMoves(current);
            broadcast(new Message(MessageType.START_MATCH).put("bet", current.room.betAmount));
        }
    }

    private void onPlayMove(Message m) throws IOException {
        if (current == null) { send(new Message(MessageType.ERROR).put("msg", "Join room first")); return; }
        Move move = (Move) m.get("move");
        synchronized (rooms) {
            if (current.p1 != null && current.p1.handler == this) current.p1.move = move;
            if (current.p2 != null && current.p2.handler == this) current.p2.move = move;
            if (current.p1 != null && current.p2 != null && current.p1.move != null && current.p2.move != null) {
                int winner = decide(current.p1.move, current.p2.move);
                try {
                    if (winner == 0) {
                        matchDAO.setWinner(current.currentMatchId, null);
                        broadcast(new Message(MessageType.MATCH_RESULT).put("result", "DRAW").put("p1", current.p1.move).put("p2", current.p2.move));
                    } else {
                        ClientHandler winH = (winner == 1 ? current.p1.handler : current.p2.handler);
                        ClientHandler loseH = (winner == 1 ? current.p2.handler : current.p1.handler);
                        int winId = winH.me.id;
                        long bet  = current.room.betAmount;
                        matchDAO.setWinner(current.currentMatchId, winId);
                        userDAO.updateBalance(winH.me.id, +bet);
                        userDAO.updateBalance(loseH.me.id, -bet);
                        userDAO.addResult(winH.me.id, true, 10);
                        userDAO.addResult(loseH.me.id, false, 10);
                        winH.me  = userDAO.findByUsername(winH.me.username);
                        loseH.me = userDAO.findByUsername(loseH.me.username);
                        broadcast(new Message(MessageType.MATCH_RESULT)
                                .put("result", "WINNER")
                                .put("winner", winH.me.username)
                                .put("p1", current.p1.move)
                                .put("p2", current.p2.move)
                                .put("bet", bet));
                    }
                } catch (Exception e) {
                    broadcast(new Message(MessageType.ERROR).put("msg", e.getMessage()));
                }
                rooms.resetMoves(current);
            }
        }
    }

    private int decide(Move a, Move b) {
        if (a == b) return 0;
        return switch (a) {
            case ROCK     -> (b == Move.SCISSORS ? 1 : 2);
            case PAPER    -> (b == Move.ROCK ? 1 : 2);
            case SCISSORS -> (b == Move.PAPER ? 1 : 2);
        };
    }

    private void onRecharge(Message m) throws IOException {
        if (me == null) { send(new Message(MessageType.ERROR).put("msg", "Login first")); return; }
        long amount = ((Number) m.get("amount")).longValue();
        if (amount <= 0) { send(new Message(MessageType.RECHARGE_FAIL).put("msg", "Amount>0")); return; }
        try {
            trxDAO.create(me.id, amount, "Recharge");
            userDAO.updateBalance(me.id, amount);
            me = userDAO.findByUsername(me.username);
            send(new Message(MessageType.RECHARGE_OK).put("balance", me.balance));
        } catch (SQLException e) {
            send(new Message(MessageType.RECHARGE_FAIL).put("msg", e.getMessage()));
        }
    }

    private void onGetRanking() throws IOException {
        try { send(new Message(MessageType.RANKING_DATA).put("users", userDAO.topRank(20))); }
        catch (SQLException e) { send(new Message(MessageType.ERROR).put("msg", e.getMessage())); }
    }

    private void broadcast(Message msg) {
        try { if (current.p1 != null) current.p1.handler.send(msg); if (current.p2 != null) current.p2.handler.send(msg); }
        catch (IOException ignored) { }
    }
}
