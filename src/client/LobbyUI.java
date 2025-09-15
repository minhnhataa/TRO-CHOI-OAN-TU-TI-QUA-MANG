package client;

import common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LobbyUI extends JFrame {
    private final User currentUser;
    private final DefaultTableModel roomModel;
    private final JTable roomTable;
    private final JLabel infoLabel;

    public LobbyUI(User user) {
        this.currentUser = user;

        setTitle("Sảnh chờ — Oẳn Tù Tì");
        setSize(640, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Header hiển thị thông tin tài khoản
        infoLabel = new JLabel(buildInfoText());
        infoLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        add(infoLabel, BorderLayout.NORTH);

        // Bảng danh sách phòng
        roomModel = new DefaultTableModel(new String[]{"ID", "Tên phòng", "Mức cược", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class;
                    case 2 -> Long.class;
                    default -> String.class;
                };
            }
        };
        roomTable = new JTable(roomModel);
        roomTable.setRowHeight(22);
        add(new JScrollPane(roomTable), BorderLayout.CENTER);

        // Panel chứa các nút
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton refreshBtn  = new JButton("Làm mới");
        JButton joinBtn     = new JButton("Vào phòng");
        JButton rechargeBtn = new JButton("Nạp tiền");
        JButton rankBtn     = new JButton("Bảng xếp hạng");
        bottom.add(refreshBtn);
        bottom.add(joinBtn);
        bottom.add(rechargeBtn);
        bottom.add(rankBtn);
        add(bottom, BorderLayout.SOUTH);

        // ====== Sự kiện các nút ======
        // Gửi yêu cầu danh sách phòng
        refreshBtn.addActionListener(e ->
            NetworkClient.getInstance().send(new Message(MessageType.INFO))
        );

        // Tham gia phòng
        joinBtn.addActionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn phòng trước");
                return;
            }
            int roomId = (Integer) roomModel.getValueAt(row, 0);
            NetworkClient.getInstance().send(new Message(MessageType.JOIN_ROOM).put("room_id", roomId));
        });

        // Nạp tiền (inline, không cần RechargeDialog)
        rechargeBtn.addActionListener(e -> doRecharge());

        // Bảng xếp hạng (inline, không cần RankDialog)
        rankBtn.addActionListener(e ->
            NetworkClient.getInstance().send(new Message(MessageType.GET_RANKING))
        );

        // ====== Nhận dữ liệu từ server ======
        // Danh sách phòng
        MessageBus.subscribe(MessageType.ROOM_LIST, msg -> {
            @SuppressWarnings("unchecked")
            List<Room> rooms = (List<Room>) msg.get("rooms");
            System.out.println("Nhận ROOM_LIST: " + (rooms == null ? 0 : rooms.size()) + " phòng");
            SwingUtilities.invokeLater(() -> updateRoomTable(rooms));
        });

        // Khi được xác nhận vào phòng
        MessageBus.subscribe(MessageType.ROOM_JOINED, msg ->
            SwingUtilities.invokeLater(() -> {
                Room room = (Room) msg.get("room");
                new GameUI(currentUser, room).setVisible(true);
            })
        );

        // Kết quả nạp tiền
        MessageBus.subscribe(MessageType.RECHARGE_OK, msg ->
            SwingUtilities.invokeLater(() -> {
                long newBalance = ((Number) msg.get("balance")).longValue();
                currentUser.balance = newBalance; // cập nhật local
                infoLabel.setText(buildInfoText());
                JOptionPane.showMessageDialog(this, "Nạp tiền thành công! Số dư mới: " + newBalance);
            })
        );

        MessageBus.subscribe(MessageType.RECHARGE_FAIL, msg ->
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, String.valueOf(msg.get("msg")),
                        "Nạp tiền thất bại", JOptionPane.ERROR_MESSAGE)
            )
        );

        // Nhận bảng xếp hạng
        MessageBus.subscribe(MessageType.RANKING_DATA, msg ->
            SwingUtilities.invokeLater(() -> {
                @SuppressWarnings("unchecked")
                List<User> users = (List<User>) msg.get("users");
                showRankDialog(users);
            })
        );
    }

    private String buildInfoText() {
        return "Xin chào " + currentUser.username +
               " | Số dư: " + currentUser.balance +
               " | Rank: " + currentUser.rankPoint;
    }

    /** Hiển thị input và gửi yêu cầu nạp tiền */
    private void doRecharge() {
        String s = JOptionPane.showInputDialog(this, "Nhập số tiền cần nạp:");
        if (s == null) return; // cancel
        s = s.trim().replaceAll("[^0-9]", "");
        if (s.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ");
            return;
        }
        long amount;
        try { amount = Long.parseLong(s); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tiền quá lớn hoặc không hợp lệ");
            return;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Số tiền phải > 0");
            return;
        }
        NetworkClient.getInstance().send(new Message(MessageType.RECHARGE).put("amount", amount));
    }

    /** Hiện bảng xếp hạng trong dialog nhỏ */
    private void showRankDialog(List<User> users) {
        String[] cols = {"#", "Tên", "Thắng", "Thua", "Rank", "Số dư"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0,2,3,4 -> Integer.class;
                    case 5 -> Long.class;
                    default -> String.class;
                };
            }
        };
        int idx = 1;
        if (users != null) {
            for (User u : users) {
                m.addRow(new Object[]{ idx++, u.username, u.totalWin, u.totalLose, u.rankPoint, u.balance });
            }
        }
        JTable tbl = new JTable(m);
        JScrollPane sp = new JScrollPane(tbl);

        JDialog d = new JDialog(this, "Bảng xếp hạng", true);
        d.setSize(520, 380);
        d.setLocationRelativeTo(this);
        d.add(sp);
        d.setVisible(true);
    }

    /** Cập nhật bảng phòng trên giao diện */
    private void updateRoomTable(List<Room> rooms) {
        roomModel.setRowCount(0);
        if (rooms == null) return;
        for (Room r : rooms) {
            roomModel.addRow(new Object[]{ r.roomId, r.roomName, r.betAmount, String.valueOf(r.status) });
        }
    }
}
