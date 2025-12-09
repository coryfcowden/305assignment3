package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Simple placeholder panel with centered string.
 */
public class DiagramPanel extends JPanel {
    Color lightOrange = new Color(255, 219, 187);

    public DiagramPanel() {
        setBackground(lightOrange);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        String s = "This space is empty for now :p";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(s)) / 2;
        int y = (getHeight() / 2) - (fm.getHeight() / 2) + fm.getAscent();
        g.setColor(Color.DARK_GRAY);
        g.drawString(s, x, y);
    }
}
