package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // ĐỔI tên DB/user/pass CHO PHÙ HỢP VỚI MÁY BẠN
    private static final String URL =
        "jdbc:mysql://localhost:3306/oantuti_game?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";        // sửa nếu bạn dùng user khác
    private static final String PASS = "123456"; // sửa thành mật khẩu thật

    static {
        try {
            // MySQL Connector/J 9.4.0 vẫn dùng driver này
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Nếu tới đây lỗi => bạn chưa add mysql-connector-j-9.4.0.jar vào Build Path
            throw new RuntimeException("Không tìm thấy MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
