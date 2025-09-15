package server;

import common.*;

import java.util.*;

public class RoomManager {

    public static class Seat {
        public ClientHandler handler;
        public Move move;
        public Seat(ClientHandler h){ this.handler = h; }
    }

    static class ActiveRoom {
        public Room room;
        public Seat p1;
        public Seat p2;
        public int currentMatchId = -1;
    }

    // Quản lý các phòng đang hoạt động
    private final Map<Integer, ActiveRoom> rooms = new HashMap<>();

    /** Nạp danh sách phòng từ DB vào bộ nhớ */
    public synchronized void loadRoomsFromDB(List<Room> list) {
        rooms.clear();
        for (Room r : list) {
            ActiveRoom ar = new ActiveRoom();
            ar.room = r;
            // trạng thái khi nạp vào bộ nhớ: cứ để WAITING/PLAYING theo DB
            rooms.put(r.roomId, ar);
        }
    }

    /** Ảnh chụp danh sách phòng (để gửi cho client) */
    public synchronized List<Room> snapshot() {
        List<Room> out = new ArrayList<>();
        for (ActiveRoom ar : rooms.values()) out.add(ar.room);
        out.sort(Comparator.comparingLong(r -> r.betAmount));
        return out;
    }

    public synchronized ActiveRoom get(int roomId) { return rooms.get(roomId); }

    public synchronized boolean joinRoom(int roomId, ClientHandler handler) {
        ActiveRoom ar = rooms.get(roomId);
        if (ar == null) return false;
        if (ar.p1 == null) { ar.p1 = new Seat(handler); ar.room.status = (ar.p2==null ? RoomStatus.WAITING : RoomStatus.PLAYING); return true; }
        if (ar.p2 == null) { ar.p2 = new Seat(handler); ar.room.status = (ar.p1==null ? RoomStatus.WAITING : RoomStatus.PLAYING); return true; }
        return false;
    }

    public synchronized void leaveRoom(ClientHandler handler) {
        for (ActiveRoom ar : rooms.values()) {
            if (ar.p1 != null && ar.p1.handler == handler) { ar.p1 = null; ar.room.status = (ar.p2==null ? RoomStatus.WAITING : RoomStatus.PLAYING); notifyOpponent(ar, handler); return; }
            if (ar.p2 != null && ar.p2.handler == handler) { ar.p2 = null; ar.room.status = (ar.p1==null ? RoomStatus.WAITING : RoomStatus.PLAYING); notifyOpponent(ar, handler); return; }
        }
    }

    public synchronized boolean bothSeated(ActiveRoom ar) {
        return ar != null && ar.p1 != null && ar.p2 != null;
    }

    public synchronized void resetMoves(ActiveRoom ar) {
        if (ar != null) {
            if (ar.p1 != null) ar.p1.move = null;
            if (ar.p2 != null) ar.p2.move = null;
        }
    }

    private void notifyOpponent(ActiveRoom ar, ClientHandler leaver) {
        try {
            if (ar.p1 != null && ar.p1.handler != leaver) ar.p1.handler.send(new Message(MessageType.OPPONENT_LEFT));
            if (ar.p2 != null && ar.p2.handler != leaver) ar.p2.handler.send(new Message(MessageType.OPPONENT_LEFT));
        } catch (Exception ignored) { }
    }
}
