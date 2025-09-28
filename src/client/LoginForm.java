package client;

import javax.swing.*;
import java.awt.*;
import common.Message;

public class LoginForm extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private GameClient client;

    public LoginForm(GameClient client) {
        this.client = client;

        setTitle("Game Oẳn Tù Tì - Đăng nhập");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel bg = new BackgroundPanel("images/nen.JPG");
        bg.setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(200, 200, 200, 180)); // nền xám mờ

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tài khoản:"), gbc);
        gbc.gridx = 1;
        txtUser = new JTextField(15);
        panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        txtPass = new JPasswordField(15);
        panel.add(txtPass, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnRegister = new JButton("Đăng ký");

        Color yellow = new Color(255, 204, 0);
        btnLogin.setBackground(yellow);
        btnRegister.setBackground(yellow);

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            client.send(new Message("LOGIN", user + ";" + pass));
        });

        btnRegister.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());
            client.send(new Message("REGISTER", user + ";" + pass));
        });

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(btnLogin, gbc);
        gbc.gridx = 1;
        panel.add(btnRegister, gbc);

        bg.add(panel);
        setContentPane(bg);
    }
}
