package client;

import common.*;
import java.io.*;
import java.net.Socket;

public class NetworkClient {
    private static NetworkClient instance;
    private final ObjectOutputStream out;

    private NetworkClient() throws IOException {
        Socket socket = new Socket("localhost", 5000);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        new Thread(() -> {
            try {
                Message msg;
                while ((msg = (Message) in.readObject()) != null) {
                    MessageBus.post(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static synchronized NetworkClient getInstance() {
        if (instance == null) {
            try { instance = new NetworkClient(); }
            catch (IOException e) { throw new RuntimeException("Không kết nối được server", e); }
        }
        return instance;
    }

    public void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
