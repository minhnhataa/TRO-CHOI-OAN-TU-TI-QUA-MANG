package client;
import common.*;
import javax.swing.*;
import java.awt.*;

public class RechargeDialog extends JDialog {
    public RechargeDialog(JFrame owner, NetworkClient net){
        super(owner, "Nạp tiền", true);
        setSize(300,120); setLocationRelativeTo(owner);
        JTextField tf = new JTextField("1000000");
        JButton ok = new JButton("Nạp");
        add(tf, BorderLayout.CENTER); add(ok, BorderLayout.SOUTH);
        ok.addActionListener(e -> { try{ long amount = Long.parseLong(tf.getText().trim()); net.send(new Message(MessageType.RECHARGE).put("amount", amount)); dispose(); }catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage()); }});
    }
}
