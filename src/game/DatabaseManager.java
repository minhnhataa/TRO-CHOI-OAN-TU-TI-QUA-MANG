package game;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:game_data.db";

    public DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found!");
        }
        createTables();
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS matches (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "player1 TEXT, player2 TEXT," +
                     "move1 TEXT, move2 TEXT, winner TEXT," +
                     "played_at DATETIME DEFAULT CURRENT_TIMESTAMP);";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertMatch(Result r) {
        String sql = "INSERT INTO matches(player1, player2, move1, move2, winner) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getPlayer1());
            ps.setString(2, r.getPlayer2());
            ps.setString(3, r.getMove1().name());
            ps.setString(4, r.getMove2().name());
            ps.setString(5, r.getWinner());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
