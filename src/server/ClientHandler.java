package server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import common.Message;
import server.dao.*;

public class ClientHandler extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private List<ClientHandler> clients;
    public String username;
    private String currentRoom;
    private String currentMove;

    private static ConcurrentHashMap<String, String> roomTurns = new ConcurrentHashMap<>();

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Message msg = (Message) in.readObject();
                handle(msg);
            }
        } catch (Exception e) {
            System.out.println("Client ngắt kết nối: " + username);
        }
    }

    private void handle(Message msg) {
        switch (msg.getType()) {
            // ==== LOGIN / REGISTER ====
            case "LOGIN": {
                String[] data = ((String) msg.getData()).split(";");
                String user = data[0];
                String pass = data[1];
                if (UserDAO.checkLogin(user, pass)) {
                    username = user;
                    if ("admin".equals(user)) {
                        send(new Message("ADMIN_LOGIN_SUCCESS", "Xin chào Admin"));
                    } else {
                        int balance = UserDAO.getBalance(user);
                        send(new Message("LOGIN_SUCCESS", user + ";" + balance));
                        broadcastRoomList();
                    }
                } else {
                    send(new Message("LOGIN_FAIL", "Sai tài khoản hoặc mật khẩu"));
                }
                break;
            }

            case "REGISTER": {
                String[] reg = ((String) msg.getData()).split(";");
                if (UserDAO.register(reg[0], reg[1])) {
                    send(new Message("REGISTER_OK", "Đăng ký thành công!"));
                } else {
                    send(new Message("REGISTER_FAIL", "Tên tài khoản đã tồn tại!"));
                }
                break;
            }

            // ==== ROOM ====
            case "CREATE_ROOM": {
                try {
                    String[] parts = ((String) msg.getData()).split(";");
                    String roomName = parts[0];
                    int bet = Integer.parseInt(parts[1]);

                    if (RoomDAO.createRoom(roomName, bet)) {
                        send(new Message("ROOM_CREATED", roomName));
                    } else {
                        send(new Message("ROOM_CREATE_FAIL", "Không thể tạo phòng (trùng tên hoặc lỗi DB)"));
                    }
                    broadcastRoomList();
                } catch (Exception e) {
                    send(new Message("ROOM_CREATE_FAIL", "Dữ liệu tạo phòng không hợp lệ!"));
                    e.printStackTrace();
                }
                break;
            }

            case "JOIN_ROOM": {
                String roomName = (String) msg.getData();
                int balance = UserDAO.getBalance(username);
                int bet = RoomDAO.getBetAmount(roomName);

                if (balance < bet) {
                    send(new Message("INFO", "❌ Không đủ tiền để vào phòng này!"));
                    return;
                }

                currentRoom = roomName;
                RoomDAO.updatePlayers(currentRoom, +1);
                roomTurns.putIfAbsent(roomName, username);

                send(new Message("INFO", "Đã vào phòng: " + currentRoom));
                broadcastRoomList();
                break;
            }

            case "LEAVE_ROOM":
                currentRoom = (String) msg.getData();
                RoomDAO.updatePlayers(currentRoom, -1);
                send(new Message("INFO", "Đã rời phòng: " + currentRoom));
                broadcastRoomList();
                break;

            // ==== MOVE ====
            case "MOVE": {
                String[] parts = ((String) msg.getData()).split(";");
                String user = parts[0];
                String room = parts[1];
                int bet = Integer.parseInt(parts[2]);
                String move = parts[3];

                this.currentRoom = room;

                String turn = roomTurns.get(room);
                if (turn != null && !turn.equals(user)) {
                    send(new Message("INFO", "⏳ Chưa đến lượt bạn!"));
                    return;
                }

                this.currentMove = move;

                ClientHandler opponent = null;
                for (ClientHandler c : clients) {
                    if (c != this && room.equals(c.currentRoom)) {
                        opponent = c;
                        break;
                    }
                }

                if (opponent != null && opponent.currentMove != null) {
                    String result1, result2;
                    if ("NONE".equals(move) && !"NONE".equals(opponent.currentMove)) {
                        result1 = "LOSE"; result2 = "WIN";
                    } else if ("NONE".equals(opponent.currentMove) && !"NONE".equals(move)) {
                        result1 = "WIN"; result2 = "LOSE";
                    } else if (move.equals(opponent.currentMove)) {
                        result1 = result2 = "DRAW";
                    } else if ((move.equals("KEO") && opponent.currentMove.equals("BAO")) ||
                               (move.equals("BUA") && opponent.currentMove.equals("KEO")) ||
                               (move.equals("BAO") && opponent.currentMove.equals("BUA"))) {
                        result1 = "WIN"; result2 = "LOSE";
                    } else {
                        result1 = "LOSE"; result2 = "WIN";
                    }

                    if (result1.equals("WIN")) {
                        UserDAO.updateWin(user, bet);
                        UserDAO.updateLose(opponent.username, bet);
                    } else if (result2.equals("WIN")) {
                        UserDAO.updateWin(opponent.username, bet);
                        UserDAO.updateLose(user, bet);
                    }

                    MatchHistoryDAO.saveHistory(user, opponent.username, result1, bet);
                    MatchHistoryDAO.saveHistory(opponent.username, user, result2, bet);

                    send(new Message("RESULT", formatResultMsg(result1, bet)));
                    opponent.send(new Message("RESULT", formatResultMsg(result2, bet)));

                    send(new Message("BALANCE_UPDATE", String.valueOf(UserDAO.getBalance(user))));
                    opponent.send(new Message("BALANCE_UPDATE", String.valueOf(UserDAO.getBalance(opponent.username))));

                    this.currentMove = null;
                    opponent.currentMove = null;
                    roomTurns.put(room, opponent.username);
                    opponent.send(new Message("INFO", "👉 Đến lượt bạn!"));
                } else {
                    if (opponent != null) {
                        roomTurns.put(room, opponent.username);
                        opponent.send(new Message("INFO", "👉 Đến lượt bạn!"));
                    }
                }
                break;
            }

            // ==== HISTORY / RANKING ====
            case "GET_HISTORY":
                send(new Message("HISTORY_DATA", MatchHistoryDAO.getHistory(username)));
                break;

            case "GET_ALL_HISTORY":
                send(new Message("ALL_HISTORY", MatchHistoryDAO.getAllHistory()));
                break;

            case "GET_RANKING":
                send(new Message("RANKING_DATA", RankingDAO.getRanking()));
                break;

            // ==== ADMIN ====
            case "ADMIN_GET_HISTORY": {
                String user = msg.getData().toString();
                send(new Message("ADMIN_HISTORY_DATA", MatchHistoryDAO.getHistory(user)));
                break;
            }

            // ==== RECHARGE ====
            case "RECHARGE_REQUEST": {
                String[] data = ((String) msg.getData()).split(";");
                String user = data[0];
                int amount = Integer.parseInt(data[1]);
                RechargeDAO.addRequest(user, amount);
                send(new Message("RECHARGE_PENDING", "Đã gửi yêu cầu nạp " + amount));
                broadcastRechargeList();
                break;
            }

            case "GET_RECHARGE_LIST":
                send(new Message("RECHARGE_LIST", RechargeDAO.getPendingRequests()));
                break;

            case "RECHARGE_APPROVE": {
                String[] parts = msg.getData().toString().split(";");
                int reqId = Integer.parseInt(parts[0]);
                String user = parts[1];
                int amount = Integer.parseInt(parts[2]);

                RechargeDAO.approveRequest(reqId);

                for (ClientHandler c : clients) {
                    if (user.equals(c.username)) {
                        c.send(new Message("BALANCE_UPDATE", String.valueOf(UserDAO.getBalance(user))));
                    }
                }

                broadcastRechargeList();
                send(new Message("RECHARGE_APPROVE", "✅ Duyệt nạp " + amount + " cho " + user));
                break;
            }

            case "RECHARGE_REJECT":
                RechargeDAO.rejectRequest(Integer.parseInt(msg.getData().toString()));
                broadcastRechargeList();
                send(new Message("RECHARGE_REJECT", "Đã từ chối yêu cầu!"));
                break;
        }
    }

    private String formatResultMsg(String result, int bet) {
        switch (result) {
            case "WIN": return "🎉 Bạn thắng! (+" + bet + ")";
            case "LOSE": return "💔 Bạn thua! (-" + bet + ")";
            default: return "🤝 Hòa nhau!";
        }
    }

    private void broadcastRoomList() {
        List<String[]> rooms = RoomDAO.getAllRooms();
        for (ClientHandler c : clients) {
            c.send(new Message("ROOM_LIST", rooms));
        }
    }

    private void broadcastRechargeList() {
        List<String[]> reqs = RechargeDAO.getPendingRequests();
        for (ClientHandler c : clients) {
            if ("admin".equals(c.username)) {
                c.send(new Message("RECHARGE_LIST", reqs));
            }
        }
    }

    public void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
