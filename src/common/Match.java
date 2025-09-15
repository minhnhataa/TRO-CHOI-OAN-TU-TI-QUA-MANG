package common;
import java.io.Serializable;

public class Match implements Serializable {
    private static final long serialVersionUID = 1L;
    public int matchId;
    public int roomId;
    public int player1Id;
    public int player2Id;
    public Integer winnerId; // null if draw
    public long betAmount;
}
