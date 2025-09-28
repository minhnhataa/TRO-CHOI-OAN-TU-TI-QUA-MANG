package server.dao;

import java.sql.*;
import java.util.*;

public class RoomDAO {

    // Tạo phòng mới (tránh trùng tên)
    public static boolean createRoom(String roomName, int betAmount) {
        try (Connection conn = DBConnection.getConnection()) {
            // Kiểm tra trùng tên
            String check = "SELECT COUNT(*) FROM rooms WHERE room_name=?";
            try (PreparedStatement psCheck = conn.prepareStatement(check)) {
                psCheck.setString(1, roomName);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            String sql = "INSERT INTO rooms(room_name, bet_amount, status, current_players, created_at) " +
                         "VALUES (?, ?, 'WAITING', 0, NOW())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roomName);
                ps.setInt(2, betAmount);
                ps.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phòng theo ID
    public static void deleteRoom(int roomId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM rooms WHERE room_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả phòng
    public static List<String[]> getAllRooms() {
        List<String[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT room_id, room_name, bet_amount, status, current_players, created_at FROM rooms";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new String[] {
                        String.valueOf(rs.getInt("room_id")),
                        rs.getString("room_name"),
                        String.valueOf(rs.getInt("bet_amount")),
                        rs.getString("status"),
                        String.valueOf(rs.getInt("current_players")),
                        rs.getString("created_at")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Cập nhật số người trong phòng (join/leave)
    public static void updatePlayers(String roomName, int delta) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT room_id, current_players FROM rooms WHERE room_name=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roomName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int roomId = rs.getInt("room_id");
                        int players = rs.getInt("current_players") + delta;
                        if (players < 0) players = 0;

                        if (players == 0) {
                            // Xóa phòng nếu không còn ai
                            deleteRoom(roomId);
                        } else {
                            String status = "WAITING";
                            if (players == 2) status = "PLAYING"; // chỉ 2 người mới chơi
                            else status = "WAITING";

                            String update = "UPDATE rooms SET current_players=?, status=? WHERE room_id=?";
                            try (PreparedStatement ps2 = conn.prepareStatement(update)) {
                                ps2.setInt(1, players);
                                ps2.setString(2, status);
                                ps2.setInt(3, roomId);
                                ps2.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Đặt trạng thái phòng thủ công
    public static void setStatus(int roomId, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE rooms SET status=? WHERE room_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, roomId);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Lấy mức cược của phòng
    public static int getBetAmount(String roomName) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT bet_amount FROM rooms WHERE room_name=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roomName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("bet_amount");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // nếu không tìm thấy phòng
    }
}
