package client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MatchHistoryForm extends JFrame {
    private static MatchHistoryForm instance;
    private DefaultTableModel model;

    public MatchHistoryForm() {
        setTitle("Lịch sử đấu");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new Object[]{"Đối thủ", "Kết quả", "Cược", "Thời gian"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);

        add(new JScrollPane(table));
    }

    public static void updateHistory(List<String[]> history) {
        SwingUtilities.invokeLater(() -> {
            if (instance == null || !instance.isDisplayable()) {
                instance = new MatchHistoryForm();
                instance.setVisible(true);
            }
            instance.model.setRowCount(0);
            for (String[] h : history) {
                instance.model.addRow(h);
            }
        });
    }
}
