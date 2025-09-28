package client;

import java.net.*;
import java.io.*;
import javax.swing.*;
import common.Message;

public class GameClient {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private int balance;
    private LobbyForm lobbyForm;
    private RoomForm currentRoomForm;

    public GameClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in  = new ObjectInputStream(socket.getInputStream());

            new Thread(this::listen).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Không kết nối được tới server!");
            e.printStackTrace();
        }
    }

    private void listen() {
        try {
            while (true) {
                Message msg = (Message) in.readObject();
                handle(msg);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Mất kết nối tới server!");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void handle(Message msg) {
        switch (msg.getType()) {
            // ==== LOGIN / REGISTER ====
            case "LOGIN_SUCCESS": {
                String data = (String) msg.getData();
                String[] parts = data.split(";");
                this.username = parts[0];
                this.balance = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;

                JOptionPane.showMessageDialog(null,
                        "Đăng nhập thành công: " + username + "\nSố dư: " + balance);

                lobbyForm = new LobbyForm(this, balance);
                lobbyForm.setVisible(true);
                break;
            }

            case "ADMIN_LOGIN_SUCCESS":
                this.username = "admin";
                JOptionPane.showMessageDialog(null, "Đăng nhập Admin thành công!");
                new AdminForm(this).setVisible(true);
                break;

            case "LOGIN_FAIL":
                JOptionPane.showMessageDialog(null, msg.getData());
                break;

            case "REGISTER_OK":
            case "REGISTER_FAIL":
                JOptionPane.showMessageDialog(null, msg.getData());
                break;

            // ==== ROOM ====
            case "ROOM_LIST":
                LobbyForm.updateRoomList((java.util.List<String[]>) msg.getData());
                break;

            case "ROOM_CREATED":
                JOptionPane.showMessageDialog(null, "Tạo phòng thành công!");
                break;

            case "ROOM_CREATE_FAIL":
                JOptionPane.showMessageDialog(null, "Không thể tạo phòng (trùng tên hoặc lỗi DB)!");
                break;

            // ==== GAME RESULT ====
            case "RESULT":
                JOptionPane.showMessageDialog(null, msg.getData());
                if (currentRoomForm != null) {
                    currentRoomForm.resetTimer();
                }
                break;

            // ==== HISTORY ====
            case "HISTORY_DATA":
                MatchHistoryForm.updateHistory((java.util.List<String[]>) msg.getData());
                break;

            case "ADMIN_HISTORY_DATA":
                AdminForm.updateHistory((java.util.List<String[]>) msg.getData());
                break;

            // ==== RANKING ====
            case "RANKING_DATA":
                RankingForm.updateRanking((java.util.List<String[]>) msg.getData());
                break;

            // ==== RECHARGE ====
            case "RECHARGE_LIST":
                AdminForm.updateRequests((java.util.List<String[]>) msg.getData());
                break;

            case "RECHARGE_APPROVE":
                JOptionPane.showMessageDialog(null, "Yêu cầu nạp tiền đã được duyệt!");
                break;

            case "RECHARGE_REJECT":
                JOptionPane.showMessageDialog(null, "Yêu cầu nạp tiền đã bị từ chối!");
                break;

            case "RECHARGE_PENDING":
            case "RECHARGE_SUCCESS":
                JOptionPane.showMessageDialog(null, msg.getData());
                break;

            // ==== BALANCE UPDATE ====
            case "BALANCE_UPDATE": {
                int newBalance = Integer.parseInt(msg.getData().toString());
                this.balance = newBalance;
                if (lobbyForm != null) {
                    lobbyForm.updateBalance(newBalance);
                }
                break;
            }

            // ==== INFO ====
            case "INFO":
                JOptionPane.showMessageDialog(null, msg.getData());
                break;

            default:
                System.out.println("⚠️ Server gửi chưa xử lý: " + msg.getType());
        }
    }

    public void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    public void setCurrentRoomForm(RoomForm rf) { this.currentRoomForm = rf; }

    public static void main(String[] args) {
        GameClient client = new GameClient("localhost", 12345);
        new LoginForm(client).setVisible(true);
    }
}
