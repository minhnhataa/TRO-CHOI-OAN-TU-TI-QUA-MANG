package common;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    public int id;
    public String username;
    public long balance;
    public int totalWin;
    public int totalLose;
    public int rankPoint;

    public User(){}
    public User(int id, String username, long balance, int totalWin, int totalLose, int rankPoint){
        this.id=id; this.username=username; this.balance=balance; this.totalWin=totalWin; this.totalLose=totalLose; this.rankPoint=rankPoint;
    }
}