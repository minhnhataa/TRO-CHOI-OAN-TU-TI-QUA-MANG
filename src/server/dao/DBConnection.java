package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/oantuti_game";
    private static final String USER = "root";       // chỉnh user MySQL của bạn
    private static final String PASS = "123456";     // chỉnh password MySQL của bạn

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
