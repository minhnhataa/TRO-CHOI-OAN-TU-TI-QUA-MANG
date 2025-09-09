package game;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> waiting = new ArrayList<>();
    private DatabaseManager db = new DatabaseManager();

    public GameServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket sock = serverSocket.accept();
                ClientHandler handler = new ClientHandler(sock, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý ghép cặp
    public synchronized void addWaiting(ClientHandler h) {
        waiting.add(h);
        if (waiting.size() >= 2) {
            ClientHandler a = waiting.remove(0);
            ClientHandler b = waiting.remove(0);
            new Thread(() -> runMatch(a, b)).start();
        }
    }

    private void runMatch(ClientHandler a, ClientHandler b) {
        try {
            a.sendMessage("MATCH_FOUND:" + b.getPlayerName());
            b.sendMessage("MATCH_FOUND:" + a.getPlayerName());

            Move m1 = (Move) a.readObject();
            Move m2 = (Move) b.readObject();

            String winner = GameLogic.decideWinner(m1.getType(), m2.getType(),
                                                   a.getPlayerName(), b.getPlayerName());

            Result r = new Result(a.getPlayerName(), b.getPlayerName(),
                                  m1.getType(), m2.getType(), winner);

            a.writeObject(r);
            b.writeObject(r);
            db.insertMatch(r);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.safeClose();
            b.safeClose();
        }
    }

    public void runMatchWithAI(ClientHandler a) {
        try {
            a.sendMessage("MATCH_FOUND:AI");
            Move m1 = (Move) a.readObject();

            Move.Type[] vals = Move.Type.values();
            Move.Type aiType = vals[new Random().nextInt(vals.length)];
            Move m2 = new Move(aiType);

            String winner = GameLogic.decideWinner(m1.getType(), m2.getType(),
                                                   a.getPlayerName(), "AI");

            Result r = new Result(a.getPlayerName(), "AI",
                                  m1.getType(), m2.getType(), winner);

            a.writeObject(r);
            db.insertMatch(r);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.safeClose();
        }
    }

    public static void main(String[] args) {
        new GameServer(20000);
    }
}
