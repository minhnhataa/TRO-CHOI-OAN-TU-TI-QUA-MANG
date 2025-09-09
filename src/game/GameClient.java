package game;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class GameClient {

    // UI
    private JFrame frame;
    private JTextArea logArea;
    private JButton btnRock, btnPaper, btnScissors, btnConnect, btnDisconnect, btnLeaveMatch;
    private JTextField txtName, txtHost, txtPort;
    private JCheckBox chkAI;

    // Network
    private volatile Socket socket;
    private volatile ObjectOutputStream out;
    private volatile ObjectInputStream in;
    private volatile boolean connected = false;

    // Player info
    private String playerName = "Player";
    private boolean playWithAI = false;

    public GameClient() {
        SwingUtilities.invokeLater(this::buildUI);
    }

    private void buildUI() {
        frame = new JFrame("Oẳn Tù Tì - Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 460);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(8, 8));

        // Top panel: connection controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        txtHost = new JTextField("localhost", 12);
        txtPort = new JTextField("20000", 6);
        txtName = new JTextField("Player" + (int)(Math.random() * 1000), 10);
        chkAI = new JCheckBox("Chơi với máy (AI)");
        btnConnect = new JButton("Kết nối");
        btnDisconnect = new JButton("Ngắt kết nối");
        btnLeaveMatch = new JButton("Rời trận");
        btnDisconnect.setEnabled(false);
        btnLeaveMatch.setEnabled(false);

        top.add(new JLabel("Host:")); top.add(txtHost);
        top.add(new JLabel("Port:")); top.add(txtPort);
        top.add(new JLabel("Tên:")); top.add(txtName);
        top.add(chkAI);
        top.add(btnConnect);
        top.add(btnDisconnect);
        top.add(btnLeaveMatch);

        frame.add(top, BorderLayout.NORTH);

        // Center: buttons (moves)
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        btnRock = new JButton("BÚA (Rock)");
        btnPaper = new JButton("BÁO (Paper)");
        btnScissors = new JButton("KÉO (Scissors)");
        btnRock.setEnabled(false); btnPaper.setEnabled(false); btnScissors.setEnabled(false);

        Dimension btnSize = new Dimension(130, 48);
        btnRock.setPreferredSize(btnSize);
        btnPaper.setPreferredSize(btnSize);
        btnScissors.setPreferredSize(btnSize);

        center.add(btnRock); center.add(btnPaper); center.add(btnScissors);
        frame.add(center, BorderLayout.CENTER);

        // Bottom: log
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(480, 200));
        frame.add(scroll, BorderLayout.SOUTH);

        // Action listeners
        btnConnect.addActionListener(e -> doConnect());
        btnDisconnect.addActionListener(e -> doFullDisconnect());
        btnLeaveMatch.addActionListener(e -> doLeaveMatch());

        btnRock.addActionListener(e -> sendMove(Move.Type.ROCK));
        btnPaper.addActionListener(e -> sendMove(Move.Type.PAPER));
        btnScissors.addActionListener(e -> sendMove(Move.Type.SCISSORS));

        frame.setVisible(true);
    }

    private void appendLog(String line) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(line + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void setConnectedState(boolean state) {
        connected = state;
        SwingUtilities.invokeLater(() -> {
            btnConnect.setEnabled(!state);
            btnDisconnect.setEnabled(state);
            btnLeaveMatch.setEnabled(state);
            btnRock.setEnabled(state);
            btnPaper.setEnabled(state);
            btnScissors.setEnabled(state);
            txtHost.setEnabled(!state);
            txtPort.setEnabled(!state);
            txtName.setEnabled(!state);
            chkAI.setEnabled(!state);
        });
    }

    private void doConnect() {
        String host = txtHost.getText().trim();
        int port;
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Port không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        playerName = txtName.getText().trim();
        if (playerName.isEmpty()) playerName = "Player" + (int)(Math.random() * 1000);
        playWithAI = chkAI.isSelected();

        appendLog("Kết nối tới " + host + ":" + port + " ...");

        // Kết nối trong thread riêng để không khóa UI
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                // IMPORTANT: tạo ObjectOutputStream trước rồi flush, rồi tạo ObjectInputStream
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                // gửi PlayerInfo đầu tiên
                PlayerInfo pi = new PlayerInfo(playerName, playWithAI);
                synchronized (out) {
                    out.writeObject(pi);
                    out.flush();
                }

                appendLog("Đã kết nối. Xin chờ server xử lý...");
                setConnectedState(true);

                // start listener
                startListener();

            } catch (Exception ex) {
                appendLog("Kết nối thất bại: " + ex.getMessage());
                closeSilently();
                setConnectedState(false);
            }
        }, "Connector-Thread").start();
    }

    private void startListener() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Object o = in.readObject(); // blocking
                    if (o == null) {
                        appendLog("Server trả về null, ngắt kết nối.");
                        break;
                    }
                    if (o instanceof String) {
                        String s = (String) o;
                        appendLog("SERVER: " + s);
                        // Server messages handling (common cases)
                        if (s.startsWith("MATCH_FOUND:")) {
                            String opp = s.substring("MATCH_FOUND:".length());
                            appendLog("Đã ghép cặp với: " + opp);
                            // enable moves
                            SwingUtilities.invokeLater(() -> {
                                btnRock.setEnabled(true);
                                btnPaper.setEnabled(true);
                                btnScissors.setEnabled(true);
                            });
                        } else if (s.startsWith("OPPONENT_LEFT:")) {
                            String who = s.substring("OPPONENT_LEFT:".length());
                            appendLog("Đối thủ rời: " + who + " — bạn sẽ được đưa về hàng chờ (nếu server hỗ trợ).");
                            // disable moves until server pairs again
                            SwingUtilities.invokeLater(() -> {
                                btnRock.setEnabled(false);
                                btnPaper.setEnabled(false);
                                btnScissors.setEnabled(false);
                            });
                        } else if (s.startsWith("LEFT_MATCH:")) {
                            appendLog("Rời trận: " + s);
                            SwingUtilities.invokeLater(() -> {
                                btnRock.setEnabled(false);
                                btnPaper.setEnabled(false);
                                btnScissors.setEnabled(false);
                            });
                        }
                    } else if (o instanceof Result) {
                        Result r = (Result) o;
                        handleResult(r);
                    } else {
                        appendLog("Nhận object khác: " + o.getClass().getName());
                    }
                }
            } catch (EOFException eof) {
                appendLog("Server đóng kết nối.");
            } catch (SocketException se) {
                appendLog("Kết nối mạng bị gián đoạn: " + se.getMessage());
            } catch (Exception ex) {
                appendLog("Lỗi listener: " + ex.getMessage());
            } finally {
                // Nếu listener dừng (server đóng socket), đảm bảo tắt UI kết nối
                SwingUtilities.invokeLater(() -> {
                    // don't send QUIT again here (we are probably already disconnected)
                    closeSilently();
                    setConnectedState(false);
                });
            }
        }, "Client-Listener");
        t.setDaemon(true);
        t.start();
    }

    private void handleResult(Result r) {
        // Cập nhật log với thông tin Result
        String winner = r.getWinner();
        String line = String.format("Trận: %s(%s) vs %s(%s) -> Người thắng: %s",
                r.getPlayer1(), r.getMove1(), r.getPlayer2(), r.getMove2(), winner);
        appendLog(line);

        // Optionally show popup nếu người chơi là người thắng hoặc thua
        if (playerName.equals(r.getPlayer1()) || playerName.equals(r.getPlayer2())) {
            if ("DRAW".equalsIgnoreCase(winner)) {
                // hòa
            } else if (winner.equals(playerName)) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Bạn thắng!"));
            } else {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, "Bạn thua!"));
            }
        }
    }

    private void sendMove(Move.Type t) {
        if (!connected || out == null) {
            appendLog("Chưa kết nối tới server.");
            return;
        }
        // gửi move trong thread nhỏ để tránh khóa UI nếu mạng chậm
        new Thread(() -> {
            try {
                Move m = new Move(t);
                synchronized (out) {
                    out.writeObject(m);
                    out.flush();
                }
                appendLog("Bạn đã chơi: " + t.name());
            } catch (Exception ex) {
                appendLog("Lỗi khi gửi nước đi: " + ex.getMessage());
            }
        }, "SendMove-" + t.name()).start();
    }

    /** Send QUIT to server but keep connection open (used for 'Leave Match') */
    private void doLeaveMatch() {
        if (!connected || out == null) {
            appendLog("Chưa kết nối.");
            return;
        }
        new Thread(() -> {
            try {
                synchronized (out) {
                    out.writeObject("QUIT");
                    out.flush();
                }
                appendLog("Đã gửi lệnh Rời trận (QUIT) tới server. Đang chờ server phản hồi...");
                // disable move buttons until server pairs again
                SwingUtilities.invokeLater(() -> {
                    btnRock.setEnabled(false);
                    btnPaper.setEnabled(false);
                    btnScissors.setEnabled(false);
                });
            } catch (Exception ex) {
                appendLog("Không thể gửi QUIT: " + ex.getMessage());
            }
        }, "LeaveMatch-Thread").start();
    }

    /** Send QUIT to server and then close connection (full disconnect) */
    private void doFullDisconnect() {
        if (connected && out != null) {
            try {
                synchronized (out) {
                    out.writeObject("QUIT");
                    out.flush();
                }
                appendLog("Đã gửi QUIT tới server trước khi đóng.");
            } catch (Exception ex) {
                appendLog("Không thể gửi QUIT trước khi đóng: " + ex.getMessage());
            }
        }
        // then close everything
        closeSilently();
        setConnectedState(false);
        appendLog("Đã ngắt kết nối.");
    }

    private void closeSilently() {
        try { if (in != null) { in.close(); } } catch (Exception ignored) {}
        try { if (out != null) { out.close(); } } catch (Exception ignored) {}
        try { if (socket != null) { socket.close(); } } catch (Exception ignored) {}
        in = null; out = null; socket = null;
        connected = false;
    }

    public static void main(String[] args) {
        // Khởi chạy client trên Event Dispatch Thread
        SwingUtilities.invokeLater(GameClient::new);
    }
}
