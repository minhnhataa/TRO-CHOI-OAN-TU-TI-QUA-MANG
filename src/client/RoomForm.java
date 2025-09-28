package client;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import common.Message;

public class RoomForm extends JFrame {
    private GameClient client;
    private String roomName;
    private Timer moveTimer;
    private boolean hasMoved = false;

    private JLabel lblCountdown;
    private JLabel lblStatus;   // 🔹 Thông báo kết quả/lượt chơi
    private int timeLeft = 15;

    public RoomForm(GameClient client, String roomName) {
        this.client = client;
        this.roomName = roomName;
        this.client.setCurrentRoomForm(this);

        setTitle("Phòng: " + roomName);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel("images/nentran.JPG");
        bg.setLayout(new BorderLayout());

        JLabel lbl = new JLabel("Phòng: " + roomName, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(Color.YELLOW);
        bg.add(lbl, BorderLayout.NORTH);

        // Panel trên cùng (đồng hồ + trạng thái)
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setOpaque(false);

        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timerPanel.setOpaque(false);
        lblCountdown = new JLabel("15");
        lblCountdown.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCountdown.setForeground(Color.WHITE);
        JLabel clockIcon = new JLabel(new ImageIcon("images/clock.png"));
        timerPanel.add(clockIcon);
        timerPanel.add(new JLabel("Thời gian: "));
        timerPanel.add(lblCountdown);

        lblStatus = new JLabel(" Đang chờ đối thủ...", JLabel.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatus.setForeground(Color.CYAN);

        topPanel.add(timerPanel);
        topPanel.add(lblStatus);
        bg.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Khu vực chọn nước đi
        JPanel movesPanel = new JPanel(new FlowLayout());
        movesPanel.setOpaque(false);

        JButton btnKeo = new JButton(new ImageIcon("images/keo.JPG"));
        JButton btnBua = new JButton(new ImageIcon("images/bua.JPG"));
        JButton btnBao = new JButton(new ImageIcon("images/bao.JPG"));

        btnKeo.addActionListener(e -> sendMove("KEO"));
        btnBua.addActionListener(e -> sendMove("BUA"));
        btnBao.addActionListener(e -> sendMove("BAO"));

        movesPanel.add(btnKeo);
        movesPanel.add(btnBua);
        movesPanel.add(btnBao);
        bg.add(movesPanel, BorderLayout.CENTER);

        JButton btnLeave = new JButton("Rời phòng");
        btnLeave.setBackground(Color.RED);
        btnLeave.setForeground(Color.WHITE);
        btnLeave.addActionListener(e -> {
            client.send(new Message("LEAVE_ROOM", roomName));
            dispose();
        });
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(btnLeave);
        bg.add(bottom, BorderLayout.SOUTH);

        setContentPane(bg);
        startTimer();
    }

    private void startTimer() {
        if (moveTimer != null) moveTimer.cancel();
        hasMoved = false;
        timeLeft = 15;
        lblCountdown.setText("" + timeLeft);

        moveTimer = new Timer();
        moveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    timeLeft--;
                    lblCountdown.setText("" + timeLeft);
                });
                if (timeLeft <= 0) {
                    moveTimer.cancel();
                    if (!hasMoved) {
                        sendMove("NONE");
                    }
                }
            }
        }, 1000, 1000);
    }

    private void sendMove(String move) {
        hasMoved = true;
        if (moveTimer != null) moveTimer.cancel();
        String data = client.getUsername() + ";" + roomName + ";1000;" + move;
        client.send(new Message("MOVE", data));
        updateStatus(" Bạn đã chọn " + move + ", chờ đối thủ...", Color.ORANGE);
    }

    public void resetTimer() {
        startTimer();
    }

    // 🔹 Cập nhật trạng thái ngay trong giao diện
    public void updateStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(msg);
            lblStatus.setForeground(color);
        });
    }
}
