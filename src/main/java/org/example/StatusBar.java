package org.example;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private final JLabel label;

    public StatusBar() {
        setLayout(new BorderLayout());
        label = new JLabel("Status: Ready");
        add(label, BorderLayout.CENTER);
    }

    public void refresh() {
        label.setText("Status: " + Blackboard.getInstance().getStatus());
    }
}
