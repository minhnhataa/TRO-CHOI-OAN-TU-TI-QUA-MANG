package game;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String playerName;
    private boolean playWithAI;

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Nhận thông tin player
            PlayerInfo pi = (PlayerInfo) in.readObject();
            playerName = pi.getName();
            playWithAI = pi.isPlayWithAI();

            if (playWithAI) {
                server.runMatchWithAI(this);
            } else {
                server.addWaiting(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void sendMessage(String msg) throws IOException {
        synchronized (out) {
            out.writeObject(msg);
            out.flush();
        }
    }

    public void writeObject(Object o) throws IOException {
        synchronized (out) {
            out.writeObject(o);
            out.flush();
        }
    }

    public Object readObject() throws Exception {
        return in.readObject();
    }

    public void safeClose() {
        try { socket.close(); } catch (Exception ignored) {}
    }
}
