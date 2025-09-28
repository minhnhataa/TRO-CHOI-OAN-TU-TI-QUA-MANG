package common;

import java.io.Serializable;

/**
 * Thông tin user (có thể dùng cho client hiển thị hoặc server truyền dữ liệu)
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private int balance;
    private int totalWin;
    private int totalLose;
    private int rankPoint;

    public User(int id, String username, int balance, int totalWin, int totalLose, int rankPoint) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.totalWin = totalWin;
        this.totalLose = totalLose;
        this.rankPoint = rankPoint;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getBalance() {
        return balance;
    }

    public int getTotalWin() {
        return totalWin;
    }

    public int getTotalLose() {
        return totalLose;
    }

    public int getRankPoint() {
        return rankPoint;
    }
}
