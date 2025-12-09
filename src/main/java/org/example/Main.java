package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for Assignment 3.
 *
 * This class sets up the entire Swing user interface, including:
 * - The URL input bar and OK button
 * - The file list panel
 * - The grid, metrics, and diagram tabs
 * - The status bar at the bottom
 *
 * It also initializes the Nanny controller, connects the GUI panels together,
 * and launches the application frame. This is the central entry point that
 * arranges all visual components and wires necessary listeners.
 *
 * @Author Cory Cowden
 */

public class Main extends JFrame {

    public Main() {
        JTextField pathField = new JTextField();
        JButton okButton = new JButton("OK");

        //use System.getenv token so multiple people can use their token
        String token = System.getenv("NANNY_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Missing environment variable NANNY_TOKEN");
        }
        Nanny controller = new Nanny(token);

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Folder Path:"), BorderLayout.WEST);
        top.add(pathField, BorderLayout.CENTER);
        top.add(okButton, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        GridPanel gridPanel = new GridPanel();
        MetricsPanel metricsPanel = new MetricsPanel();
        DiagramPanel diagramPanel = new DiagramPanel();

        tabs.addTab("Grid", gridPanel);
        tabs.addTab("Metrics", metricsPanel);
        tabs.addTab("Diagram", diagramPanel);

        FilePanel filePanel = new FilePanel();
        filePanel.setPreferredSize(new Dimension(250, 600));

        StatusBar statusBar = new StatusBar();
        statusBar.setPreferredSize(new Dimension(0, 24));

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(filePanel, BorderLayout.WEST);
        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        okButton.addActionListener(new Listener(pathField, controller, gridPanel, filePanel, statusBar, metricsPanel));

        Blackboard.getInstance();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.setTitle("Assignment 3");
        main.setSize(1000, 600);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }
}
