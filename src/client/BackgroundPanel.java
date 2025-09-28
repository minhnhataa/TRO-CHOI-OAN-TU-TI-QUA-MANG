package client;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(String path) {
        try {
            background = new ImageIcon(path).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
