package client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import common.Message;

public class LobbyForm extends JFrame {
    private static DefaultTableModel model;
    private GameClient client;
    private JLabel lblUser;
    private JLabel lblBalance;

    public LobbyForm(GameClient client, int balance) {
        this.client = client;

        setTitle("Sảnh chờ - Oẳn Tù Tì");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 📌 Background
        BackgroundPanel bg = new BackgroundPanel("images/anhnen.jpg"); 
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        // 📌 Header (username + balance có icon)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        top.setOpaque(false);

        lblUser = new JLabel(client.getUsername(), UIManager.getIcon("FileView.fileIcon"), JLabel.LEFT);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(new Color(178, 34, 34)); // đỏ đậm

        lblBalance = new JLabel(String.valueOf(balance), UIManager.getIcon("FileView.hardDriveIcon"), JLabel.LEFT);
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBalance.setForeground(new Color(178, 34, 34)); // đỏ đậm

        top.add(lblUser);
        top.add(new JLabel("|"));
        top.add(lblBalance);

        bg.add(top, BorderLayout.NORTH);

        // 📌 Bảng phòng
        model = new DefaultTableModel(
                new Object[]{"ID", "Tên phòng", "Mức cược", "Trạng thái", "Người chơi", "Tạo lúc"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(60, 63, 65));
        table.getTableHeader().setForeground(Color.WHITE);

        // 📌 Highlight trạng thái
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String status = (String) table.getValueAt(row, 3);
                if ("WAITING".equals(status)) {
                    c.setBackground(new Color(255, 255, 180));
                } else if ("PLAYING".equals(status)) {
                    c.setBackground(new Color(200, 255, 200));
                } else if ("FULL".equals(status)) {
                    c.setBackground(new Color(255, 200, 200));
                } else {
                    c.setBackground(Color.WHITE);
                }
                if (isSelected) {
                    c.setBackground(new Color(100, 149, 237)); // xanh khi chọn
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        bg.add(scroll, BorderLayout.CENTER);

        // 📌 Nút chức năng
        JButton btnJoin = createButton("Vào phòng");
        JButton btnCreate = createButton("Tạo phòng");
        JButton btnRanking = createButton("Bảng xếp hạng");
        JButton btnHistory = createButton("Lịch sử đấu");
        JButton btnRecharge = createButton("Nạp tiền");

        // Join phòng
        btnJoin.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String roomName = (String) model.getValueAt(row, 1);
                client.send(new Message("JOIN_ROOM", roomName));
                new RoomForm(client, roomName).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng!");
            }
        });

        // Tạo phòng
        btnCreate.addActionListener(e -> {
            JTextField txtName = new JTextField();
            JTextField txtBet = new JTextField("1000");

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Tên phòng:"));
            panel.add(txtName);
            panel.add(new JLabel("Mức cược:"));
            panel.add(txtBet);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Tạo phòng mới", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String roomName = txtName.getText().trim();
                String betStr = txtBet.getText().trim();

                if (!roomName.isEmpty() && betStr.matches("\\d+")) {
                    int bet = Integer.parseInt(betStr);
                    client.send(new Message("CREATE_ROOM", roomName + ";" + bet));
                } else {
                    JOptionPane.showMessageDialog(this, "Tên phòng hoặc mức cược không hợp lệ!");
                }
            }
        });

        // Bảng xếp hạng
        btnRanking.addActionListener(e -> {
            client.send(new Message("GET_RANKING", null));
            new RankingForm(client).setVisible(true);
        });

        // Lịch sử đấu
        btnHistory.addActionListener(e -> {
            client.send(new Message("GET_HISTORY", null));
        });

        // Nạp tiền
        btnRecharge.addActionListener(e -> {
            new RechargeForm(client).setVisible(true);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottom.setOpaque(false);
        bottom.add(btnJoin);
        bottom.add(btnCreate);
        bottom.add(btnRanking);
        bottom.add(btnHistory);
        bottom.add(btnRecharge);

        bg.add(bottom, BorderLayout.SOUTH);
    }

    // 📌 Helper tạo nút
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }

    // 📌 Cập nhật danh sách phòng
    public static void updateRoomList(List<String[]> rooms) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            for (String[] r : rooms) {
                model.addRow(r);
            }
        });
    }

    // 📌 Cập nhật balance khi có BALANCE_UPDATE
    public void updateBalance(int newBalance) {
        SwingUtilities.invokeLater(() -> {
            lblBalance.setText(String.valueOf(newBalance));
        });
    }
}
