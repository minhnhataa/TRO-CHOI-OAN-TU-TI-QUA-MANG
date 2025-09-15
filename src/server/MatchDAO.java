package server;
import java.sql.*;

public class MatchDAO {
    public int create(int roomId, int p1, int p2, long bet) throws SQLException {
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement("INSERT INTO Match(room_id, player1_id, player2_id, bet_amount) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, roomId); ps.setInt(2, p1); ps.setInt(3, p2); ps.setLong(4, bet); ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
    public void setWinner(int matchId, Integer winnerId) throws SQLException {
        try(Connection c = Database.getConnection();
            PreparedStatement ps = c.prepareStatement("UPDATE Match SET winner_id=? WHERE match_id=?")){
            if(winnerId==null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, winnerId);
            ps.setInt(2, matchId); ps.executeUpdate();
        }
    }
}
