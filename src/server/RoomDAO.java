package server;

import common.Room;
import common.RoomStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    /** Đọc tất cả phòng từ DB (sắp xếp theo bet) */
    public List<Room> listAll() throws SQLException {
        String sql = "SELECT room_id, room_name, bet_amount, status FROM Room ORDER BY bet_amount ASC";
        List<Room> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_name"),
                        rs.getLong("bet_amount"),
                        RoomStatus.valueOf(rs.getString("status"))
                );
                list.add(r);
            }
        }
        return list;
    }

    /** Đảm bảo có đủ 5 phòng mặc định trong DB */
    public void ensureDefaultRooms() throws SQLException {
        long[] tiers = {5_000_000L, 10_000_000L, 15_000_000L, 20_000_000L, 50_000_000L};
        try (Connection c = Database.getConnection()) {
            for (long t : tiers) {
                try (PreparedStatement check = c.prepareStatement("SELECT 1 FROM Room WHERE bet_amount=?")) {
                    check.setLong(1, t);
                    try (ResultSet rs = check.executeQuery()) {
                        if (!rs.next()) {
                            try (PreparedStatement ins = c.prepareStatement(
                                    "INSERT INTO Room(room_name, bet_amount, status) VALUES(?, ?, 'WAITING')")) {
                                ins.setString(1, "Phòng " + (t / 1_000_000) + "tr");
                                ins.setLong(2, t);
                                ins.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }
}
