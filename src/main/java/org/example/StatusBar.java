package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Simple status bar displayed at the bottom of the application.
 *
 * It shows the current status message stored in the Blackboard, updating
 * whenever the user loads files, encounters an error, or completes analysis.
 * @Author Cory Cowden
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
