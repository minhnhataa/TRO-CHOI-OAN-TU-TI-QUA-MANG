package common;

import java.io.Serializable;

/**
 * Lưu thông tin lịch sử trận đấu để gửi qua mạng
 */
public class MatchHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String opponent;
    private String result;
    private int betAmount;
    private String playedAt;

    public MatchHistory(String opponent, String result, int betAmount, String playedAt) {
        this.opponent = opponent;
        this.result = result;
        this.betAmount = betAmount;
        this.playedAt = playedAt;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getResult() {
        return result;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public String getPlayedAt() {
        return playedAt;
    }
}
