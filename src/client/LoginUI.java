package client;

import common.*;
import javax.swing.*;

public class LoginUI extends JFrame {
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginUI() {
        setTitle("Đăng nhập Oẳn Tù Tì");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.add(new JLabel("Tên đăng nhập:"));
        p.add(usernameField);
        p.add(new JLabel("Mật khẩu:"));
        p.add(passwordField);

        JButton loginBtn = new JButton("Đăng nhập");
        JButton registerBtn = new JButton("Đăng ký");
        p.add(loginBtn);
        p.add(registerBtn);
        add(p);

        loginBtn.addActionListener(e -> {
            String u = usernameField.getText().trim();
            String pss = new String(passwordField.getPassword());
            if (u.isEmpty() || pss.isEmpty()) return;
            NetworkClient.getInstance().send(new Message(MessageType.LOGIN)
                    .put("username", u)
                    .put("password_hash", pss));
        });

        registerBtn.addActionListener(e -> {
            String u = usernameField.getText().trim();
            String pss = new String(passwordField.getPassword());
            if (u.isEmpty() || pss.isEmpty()) return;
            NetworkClient.getInstance().send(new Message(MessageType.REGISTER)
                    .put("username", u)
                    .put("password_hash", pss));
        });

        MessageBus.subscribe(MessageType.LOGIN_OK, msg ->
            SwingUtilities.invokeLater(() -> {
                new LobbyUI((User) msg.get("user")).setVisible(true);
                dispose();
            })
        );

        MessageBus.subscribe(MessageType.LOGIN_FAIL,
            msg -> JOptionPane.showMessageDialog(this, msg.get("msg"), "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE));
    }
}
