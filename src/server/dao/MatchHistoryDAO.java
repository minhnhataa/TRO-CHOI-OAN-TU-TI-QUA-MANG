package server.dao;

import java.sql.*;
import java.util.*;

public class MatchHistoryDAO {
    public static void saveHistory(String username, String opponent, String result, int bet) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO match_history(user_id, opponent, result, bet_amount) " +
                         "VALUES ((SELECT id FROM users WHERE username=?), ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, opponent);
            ps.setString(3, result);
            ps.setInt(4, bet);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static List<String[]> getHistory(String username) {
        List<String[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT opponent, result, bet_amount, played_at " +
                         "FROM match_history m JOIN users u ON m.user_id=u.id " +
                         "WHERE u.username=? ORDER BY played_at DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[] {
                    rs.getString("opponent"),
                    rs.getString("result"),
                    rs.getString("bet_amount"),
                    rs.getString("played_at")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Admin dùng để xem tất cả lịch sử
    public static List<String[]> getAllHistory() {
        List<String[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.username, opponent, result, bet_amount, played_at " +
                         "FROM match_history m JOIN users u ON m.user_id=u.id " +
                         "ORDER BY played_at DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[] {
                    rs.getString("username"),
                    rs.getString("opponent"),
                    rs.getString("result"),
                    rs.getString("bet_amount"),
                    rs.getString("played_at")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
