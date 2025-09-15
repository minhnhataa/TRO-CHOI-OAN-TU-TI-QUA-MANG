package server;
import java.sql.*;

public class TransactionDAO {
    public void create(int userId, long amount, String note) throws SQLException {
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO Transaction(user_id, amount, note) VALUES(?,?,?)")){
            ps.setInt(1, userId); ps.setLong(2, amount); ps.setString(3, note); ps.executeUpdate();
        }
    }
}