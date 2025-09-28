package client;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import common.Message;

public class AdminForm extends JFrame {
    private GameClient client;
    private static DefaultTableModel rechargeModel;
    private static DefaultTableModel rankModel;
    private static DefaultTableModel historyModel;

    public AdminForm(GameClient client) {
        this.client = client;
        setTitle("Quản trị viên - Oẳn Tù Tì");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets.left = 10;
            }
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement,
                                              int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                g.setColor(isSelected ? new Color(255, 204, 0) : new Color(255, 230, 128));
                g.fillRect(x, y, w, h);
            }
            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                               Rectangle[] rects, int tabIndex,
                                               Rectangle iconRect, Rectangle textRect, boolean isSelected) {}
        });

        // ================= TAB BẢNG XẾP HẠNG =================
        rankModel = new DefaultTableModel(new Object[]{"Người chơi", "Điểm"}, 0);
        JTable rankTable = new JTable(rankModel);
        JPanel rankPanel = new JPanel(new BorderLayout());
        rankPanel.setBackground(new Color(255, 204, 0));
        rankPanel.add(new JScrollPane(rankTable), BorderLayout.CENTER);
        tabs.addTab("Bảng xếp hạng", rankPanel);

        // ================= TAB LỊCH SỬ ĐẤU =================
        historyModel = new DefaultTableModel(new Object[]{"Đối thủ", "Kết quả", "Cược", "Thời gian"}, 0);
        JTable historyTable = new JTable(historyModel);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(255, 204, 0));

        JTextField txtUser = new JTextField();
        JButton btnLoad = new JButton("Xem lịch sử");
        btnLoad.setBackground(new Color(255, 204, 0));

        btnLoad.addActionListener(e -> {
            String user = txtUser.getText().trim();
            if (!user.isEmpty()) {
                client.send(new Message("ADMIN_GET_HISTORY", user));
            }
        });

        JPanel topHistory = new JPanel(new BorderLayout());
        topHistory.setOpaque(false);
        topHistory.add(new JLabel("Tên người chơi:"), BorderLayout.WEST);
        topHistory.add(txtUser, BorderLayout.CENTER);
        topHistory.add(btnLoad, BorderLayout.EAST);

        historyPanel.add(topHistory, BorderLayout.NORTH);
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        tabs.addTab("Lịch sử đấu", historyPanel);

        // ================= TAB DUYỆT NẠP TIỀN =================
        rechargeModel = new DefaultTableModel(new Object[]{"ID", "Người chơi", "Số tiền"}, 0);
        JTable rechargeTable = new JTable(rechargeModel);

        JButton btnApprove = new JButton("Duyệt");
        JButton btnReject = new JButton("Từ chối");

        Color yellow = new Color(255, 204, 0);
        btnApprove.setBackground(yellow);
        btnReject.setBackground(yellow);

        btnApprove.addActionListener(e -> {
            int row = rechargeTable.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(rechargeModel.getValueAt(row, 0).toString());
                String user = rechargeModel.getValueAt(row, 1).toString();
                int amount = Integer.parseInt(rechargeModel.getValueAt(row, 2).toString());
                client.send(new Message("RECHARGE_APPROVE", id + ";" + user + ";" + amount));
            }
        });

        btnReject.addActionListener(e -> {
            int row = rechargeTable.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(rechargeModel.getValueAt(row, 0).toString());
                client.send(new Message("RECHARGE_REJECT", String.valueOf(id)));
            }
        });

        JPanel rechargePanel = new JPanel(new BorderLayout());
        rechargePanel.setBackground(yellow);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(btnApprove);
        btnPanel.add(btnReject);

        rechargePanel.add(new JScrollPane(rechargeTable), BorderLayout.CENTER);
        rechargePanel.add(btnPanel, BorderLayout.SOUTH);

        tabs.addTab("Duyệt nạp tiền", rechargePanel);

        add(tabs);
    }

    public static void updateRequests(List<String[]> reqs) {
        SwingUtilities.invokeLater(() -> {
            rechargeModel.setRowCount(0);
            for (String[] r : reqs) {
                rechargeModel.addRow(r);
            }
        });
    }

    public static void updateRanking(List<String[]> ranks) {
        SwingUtilities.invokeLater(() -> {
            rankModel.setRowCount(0);
            for (String[] r : ranks) {
                rankModel.addRow(r);
            }
        });
    }

    public static void updateHistory(List<String[]> history) {
        SwingUtilities.invokeLater(() -> {
            historyModel.setRowCount(0);
            for (String[] h : history) {
                historyModel.addRow(h);
            }
        });
    }
}
