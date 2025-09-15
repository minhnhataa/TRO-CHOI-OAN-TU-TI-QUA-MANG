package client;

import common.*;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {
    private final User me;
    private final Room room;

    private final JLabel status = new JLabel("Chờ trận...");
    private final JButton btnRock     = new JButton("Búa");
    private final JButton btnPaper    = new JButton("Bao");
    private final JButton btnScissors = new JButton("Kéo");

    public GameUI(User me, Room room) {
        this.me = me;
        this.room = room;

        setTitle("Phòng " + room.roomName + " | Cược: " + room.betAmount);
        setSize(420, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(status, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,3,10,10));
        center.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        center.add(btnRock);
        center.add(btnPaper);
        center.add(btnScissors);
        add(center, BorderLayout.CENTER);

        // Mặc định: chờ START_MATCH từ server mới mở nút
        setMoveButtonsEnabled(false);

        // Gửi nước đi
        btnRock.addActionListener(e -> sendMove(Move.ROCK));
        btnPaper.addActionListener(e -> sendMove(Move.PAPER));
        btnScissors.addActionListener(e -> sendMove(Move.SCISSORS));

        // Nhận sự kiện từ server
        MessageBus.subscribe(MessageType.START_MATCH, msg ->
            SwingUtilities.invokeLater(() -> {
                Long bet = (Long) msg.get("bet");
                status.setText("Trận mới! Cược: " + (bet == null ? room.betAmount : bet) + ". Hãy chọn nước đi.");
                setMoveButtonsEnabled(true);
            })
        );

        MessageBus.subscribe(MessageType.MATCH_RESULT, msg ->
            SwingUtilities.invokeLater(() -> {
                String result = (String) msg.get("result");
                String winner = (String) msg.get("winner"); // có thể null nếu hòa
                Move p1 = (Move) msg.get("p1");
                Move p2 = (Move) msg.get("p2");
                String text;
                if ("DRAW".equals(result)) {
                    text = "Hòa! (" + p1 + " vs " + p2 + ")";
                } else {
                    text = "Kết quả: " + winner + " thắng (" + p1 + " vs " + p2 + ")";
                }
                status.setText(text + ". Chờ trận mới...");
                setMoveButtonsEnabled(false);
            })
        );

        MessageBus.subscribe(MessageType.OPPONENT_LEFT, msg ->
            SwingUtilities.invokeLater(() -> {
                status.setText("Đối thủ rời phòng. Chờ người khác...");
                setMoveButtonsEnabled(false);
            })
        );
    }

    private void sendMove(Move m) {
        setMoveButtonsEnabled(false); // gửi xong khóa lại để không spam
        NetworkClient.getInstance().send(
                new Message(MessageType.PLAY_MOVE).put("move", m)
        );
        status.setText("Đã chọn: " + m + ". Chờ đối thủ...");
    }

    private void setMoveButtonsEnabled(boolean en) {
        btnRock.setEnabled(en);
        btnPaper.setEnabled(en);
        btnScissors.setEnabled(en);
    }
}
