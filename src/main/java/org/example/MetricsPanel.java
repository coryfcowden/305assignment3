package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Swing visualization panel that renders classes on an
 * Abstractness–Instability chart. Each class appears as a plotted
 * point, with labels derived from simplified class names.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class MetricsPanel extends JPanel {

    Color lightPink = new Color(255, 213, 229);

    @Override
    protected void paintComponent(Graphics g) {
        setBackground(lightPink);
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        g.setColor(Color.BLACK);
        g.drawLine(0, h, w, 0);

        g.setColor(Color.BLACK);
        g.drawString("Useless", w - 60, 20);
        g.drawString("Painful", 10, h - 10);


        Map<String, Metric> metrics = Blackboard.getInstance().getMetrics();
        if (metrics == null || metrics.isEmpty()) return;

        for (Metric m : metrics.values()) {

            double instability = clamp01(m.getInstability());
            double abstractness = clamp01(m.getAbstractness());

            int x = (int) (instability * (w - 40)) + 20;
            int y = (int) ((1 - abstractness) * (h - 40)) + 20;

            g.setColor(Color.GRAY);
            g.fillOval(x - 6, y - 6, 12, 12);

            String rawName = m.getClassName();
            String cleanName = simplifyName(rawName);

            g.setColor(Color.BLACK);
            int strW = g.getFontMetrics().stringWidth(cleanName);
            g.drawString(cleanName, x - strW / 2, y - 10);
        }
    }

    private String simplifyName(String fullName) {
        if (fullName == null) return "";

        String name = fullName;

        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash != -1) {
            name = name.substring(slash + 1);
        }

        if (name.contains(".")) {
            String[] parts = name.split("\\.");
            name = parts[parts.length - 1];
        }

        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }

        return name;
    }

    private double clamp01(double v) {
        if (Double.isNaN(v) || v < 0) return 0.0;
        if (v > 1) return 1.0;
        return v;
    }
}
