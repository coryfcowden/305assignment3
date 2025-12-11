package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

private static final Logger logger = LoggerFactory.getLogger(Main.class);

public Main() {
    logger.info("Creating main application window");

    JTextField pathField = new JTextField();
    JButton okButton = new JButton("OK");

    Nanny controller = new Nanny("");
    logger.debug("Initialized Nanny controller");

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

    logger.info("Main window created");
    }

    public static void main(String[] args) {
        logger.info("Starting Assignment 3 application");
        Main main = new Main();
        main.setTitle("Assignment 3");
        main.setSize(1000, 600);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setVisible(true);
    }
}