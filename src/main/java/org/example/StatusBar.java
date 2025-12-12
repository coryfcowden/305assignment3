package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Swing status bar displaying the application's status value
 * stored in the Blackboard. Refresh updates the label text.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

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
