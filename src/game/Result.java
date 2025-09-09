package game;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private String player1, player2;
    private Move.Type move1, move2;
    private String winner;

    public Result(String p1, String p2, Move.Type m1, Move.Type m2, String winner) {
        this.player1 = p1;
        this.player2 = p2;
        this.move1 = m1;
        this.move2 = m2;
        this.winner = winner;
    }

    public String getPlayer1() { return player1; }
    public String getPlayer2() { return player2; }
    public Move.Type getMove1() { return move1; }
    public Move.Type getMove2() { return move2; }
    public String getWinner() { return winner; }

    @Override
    public String toString() {
        return player1 + "(" + move1 + ") vs " + player2 + "(" + move2 + ") => Winner: " + winner;
    }
}
