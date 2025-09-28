package server.dao;

import java.sql.*;
import java.util.*;

public class RechargeDAO {
    // Người chơi gửi yêu cầu nạp
    public static void addRequest(String username, int amount) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO recharge_requests(user_id, amount, status) " +
                         "VALUES ((SELECT id FROM users WHERE username=?), ?, 'PENDING')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setInt(2, amount);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Lấy danh sách các yêu cầu PENDING cho admin
    public static List<String[]> getPendingRequests() {
        List<String[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.request_id, u.username, r.amount, r.status " +
                         "FROM recharge_requests r JOIN users u ON r.user_id=u.id " +
                         "WHERE r.status='PENDING'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("request_id"),
                    rs.getString("username"),
                    rs.getString("amount"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Duyệt nạp tiền
    public static void approveRequest(int requestId) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Lấy user_id và số tiền
            String q = "SELECT user_id, amount FROM recharge_requests WHERE request_id=?";
            PreparedStatement ps1 = conn.prepareStatement(q);
            ps1.setInt(1, requestId);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                int amount = rs.getInt("amount");

                // Cộng tiền vào users
                String updUser = "UPDATE users SET balance = balance + ? WHERE id=?";
                PreparedStatement ps2 = conn.prepareStatement(updUser);
                ps2.setInt(1, amount);
                ps2.setInt(2, userId);
                ps2.executeUpdate();

                // Cập nhật trạng thái request
                String updReq = "UPDATE recharge_requests SET status='APPROVED' WHERE request_id=?";
                PreparedStatement ps3 = conn.prepareStatement(updReq);
                ps3.setInt(1, requestId);
                ps3.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Từ chối nạp tiền
    public static void rejectRequest(int requestId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE recharge_requests SET status='REJECTED' WHERE request_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, requestId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 🔹 Lấy username từ requestId
    public static String getUserByRequest(int requestId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.username " +
                         "FROM recharge_requests r JOIN users u ON r.user_id=u.id " +
                         "WHERE r.request_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
