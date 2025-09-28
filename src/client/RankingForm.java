package client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class RankingForm extends JFrame {
    private static DefaultTableModel model;
    private static final String ICON_DIR = "images/";

    public RankingForm(GameClient client) {
        setTitle("Bảng xếp hạng");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lbl = new JLabel("BẢNG XẾP HẠNG", JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.BLACK);

        String[] cols = {"Top", "Người chơi", "Rank Point", "Thắng", "Thua", "Tỉ lệ thắng", "Số dư"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                return String.class;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(50);

        // Renderer cho icon Top
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel lbl = new JLabel((ImageIcon) value);
                    lbl.setHorizontalAlignment(JLabel.CENTER);
                    return lbl;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // Căn giữa cho cột chữ/số
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Highlight top1-3
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (row == 0) c.setBackground(new Color(255, 215, 0));
                else if (row == 1) c.setBackground(new Color(192, 192, 192));
                else if (row == 2) c.setBackground(new Color(205, 127, 50));
                else c.setBackground(Color.WHITE);
                return c;
            }
        });

        add(lbl, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Load icon ngoài project
    private static ImageIcon loadIcon(String filename, int w, int h) {
        File file = new File(ICON_DIR + filename);
        if (!file.exists()) return null;
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // Update BXH từ server
    public static void updateRanking(List<String[]> data) {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            int rank = 1;
            for (String[] row : data) {
                ImageIcon icon = null;
                if (rank == 1) icon = loadIcon("top1.jpg", 40, 40);
                else if (rank == 2) icon = loadIcon("top2.jpg", 40, 40);
                else if (rank == 3) icon = loadIcon("top3.jpg", 40, 40);

                Object topDisplay = (icon != null) ? icon : String.valueOf(rank);
                model.addRow(new Object[]{
                        topDisplay, row[1], row[2], row[3], row[4], row[5], row[6]
                });
                rank++;
            }
        });
    }
}
