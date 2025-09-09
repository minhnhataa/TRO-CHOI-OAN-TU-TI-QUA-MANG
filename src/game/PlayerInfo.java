package game;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private boolean playWithAI;

    public PlayerInfo(String name, boolean playWithAI) {
        this.name = name;
        this.playWithAI = playWithAI;
    }

    public String getName() { return name; }
    public boolean isPlayWithAI() { return playWithAI; }
}
