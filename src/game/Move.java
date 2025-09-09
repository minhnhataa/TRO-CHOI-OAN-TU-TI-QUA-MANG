package game;

import java.io.Serializable;

public class Move implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { ROCK, PAPER, SCISSORS }

    private Type type;

    public Move(Type type) { this.type = type; }
    public Type getType() { return type; }
}
