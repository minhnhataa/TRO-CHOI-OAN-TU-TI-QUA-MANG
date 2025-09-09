package game;

public class GameLogic {
    public static String decideWinner(Move.Type m1, Move.Type m2, String p1, String p2) {
        if (m1 == m2) return "DRAW";

        switch (m1) {
            case ROCK:     return (m2 == Move.Type.SCISSORS) ? p1 : p2;
            case PAPER:    return (m2 == Move.Type.ROCK) ? p1 : p2;
            case SCISSORS: return (m2 == Move.Type.PAPER) ? p1 : p2;
        }
        return "DRAW";
    }
}
