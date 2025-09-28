package server.dao;

import java.sql.*;

public class UserDAO {

    public static boolean checkLogin(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean register(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String check = "SELECT id FROM users WHERE username=?";
            PreparedStatement psCheck = conn.prepareStatement(check);
            psCheck.setString(1, username);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) return false;

            String sql = "INSERT INTO users(username, password, balance, total_win, total_lose, rank_point) VALUES (?,?,10000,0,0,0)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserId(String username) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    public static int getBalance(String username) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT balance FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("balance");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updateBalance(String username, int delta) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE users SET balance = balance + ? WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, delta);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateWin(String username, int bet) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE users SET balance=balance+?, total_win=total_win+1, rank_point=rank_point+10 WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bet);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void updateLose(String username, int bet) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE users SET balance=balance-?, total_lose=total_lose+1, rank_point=GREATEST(rank_point-5,0) WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bet);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
