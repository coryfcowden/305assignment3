package org.example;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        JTextField pathField = new JTextField();
        JButton okButton = new JButton("OK");

        Nanny controller = new Nanny("TOKEN");

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

        okButton.addActionListener(new Listener(pathField, controller, gridPanel, filePanel, statusBar, metricsPanel, diagramPanel));

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
