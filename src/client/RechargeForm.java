package client;

import javax.swing.*;
import java.awt.*;
import common.Message;

public class RechargeForm extends JFrame {
    private JTextField txtAmount;
    private GameClient client;

    public RechargeForm(GameClient client) {
        this.client = client;

        setTitle("Nạp tiền");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel lbl = new JLabel("Số tiền muốn nạp:");
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtAmount = new JTextField();
        txtAmount.setMaximumSize(new Dimension(200, 30));

        JButton btnSend = new JButton("Gửi yêu cầu");
        btnSend.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnSend.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(txtAmount.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Số tiền phải lớn hơn 0!");
                    return;
                }

                // Gửi yêu cầu nạp về server
                client.send(new Message("RECHARGE_REQUEST",
                        client.getUsername() + ";" + amount));

                JOptionPane.showMessageDialog(this, "✅ Đã gửi yêu cầu nạp " + amount);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!");
            }
        });

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(10));
        panel.add(txtAmount);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnSend);

        add(panel);
    }
}
