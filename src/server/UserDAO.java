package server;
import common.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User findByUsername(String username) throws SQLException {
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM User WHERE username=?")){
            ps.setString(1, username);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) return map(rs);
                return null;
            }
        }
    }

    public User create(String username, String passHash) throws SQLException {
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO User(username, password_hash, balance) VALUES(?,?,0)", Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, username);
            ps.setString(2, passHash);
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()){
                    int id = keys.getInt(1);
                    return new User(id, username, 0, 0, 0, 0);
                }
            }
        }
        return null;
    }

    public boolean checkLogin(String username, String passHash) throws SQLException {
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT 1 FROM User WHERE username=? AND password_hash=?")){
            ps.setString(1, username);
            ps.setString(2, passHash);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }

    public void updateBalance(int userId, long delta) throws SQLException {
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE User SET balance = balance + ? WHERE user_id=?")){
            ps.setLong(1, delta); ps.setInt(2, userId); ps.executeUpdate();
        }
    }

    public void addResult(int userId, boolean win, int rankDelta) throws SQLException {
        String sql = win ? "UPDATE User SET total_win=total_win+1, rank_point=rank_point+? WHERE user_id=?"
                         : "UPDATE User SET total_lose=total_lose+1, rank_point=GREATEST(rank_point-?,0) WHERE user_id=?";
        try(Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, Math.abs(rankDelta)); ps.setInt(2, userId); ps.executeUpdate();
        }
    }

    public List<User> topRank(int limit) throws SQLException {
        List<User> list = new ArrayList<>();
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM User ORDER BY rank_point DESC, total_win DESC LIMIT ?")){
            ps.setInt(1, limit);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getLong("balance"),
            rs.getInt("total_win"),
            rs.getInt("total_lose"),
            rs.getInt("rank_point")
        );
    }
}