package server.dao;

import java.sql.*;
import java.util.*;

public class RankingDAO {

    // Lấy BXH top 20 theo rank_point + thêm winrate + balance
    public static List<String[]> getRanking() {
        List<String[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username, rank_point, total_win, total_lose, balance " +
                         "FROM users ORDER BY rank_point DESC LIMIT 20";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int rank = 1;
            while (rs.next()) {
                int win = rs.getInt("total_win");
                int lose = rs.getInt("total_lose");
                int total = win + lose;
                String winrate = (total > 0) ? (win * 100 / total) + "%" : "0%";

                list.add(new String[]{
                        String.valueOf(rank),                     // Thứ hạng
                        rs.getString("username"),                 // Người chơi
                        String.valueOf(rs.getInt("rank_point")),  // Điểm rank
                        String.valueOf(win),                      // Tổng thắng
                        String.valueOf(lose),                     // Tổng thua
                        winrate,                                  // Tỉ lệ thắng
                        String.valueOf(rs.getInt("balance"))      // Số dư
                });
                rank++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Cập nhật điểm rank
    public static void updateRank(String username, int delta) {
        try (Connection conn = DBConnection.getConnection()) {
            String sqlGet = "SELECT rank_point FROM users WHERE username=?";
            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setString(1, username);
            ResultSet rs = psGet.executeQuery();

            if (rs.next()) {
                int current = rs.getInt("rank_point");
                int newRank = current + delta;
                if (newRank < 0) newRank = 0;

                String sql = "UPDATE users SET rank_point=? WHERE username=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, newRank);
                ps.setString(2, username);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
