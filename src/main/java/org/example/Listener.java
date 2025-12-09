package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the action for the top "OK" button.
 *
 * This controller:
 * - Reads the GitHub folder URL
 * - Uses the Nanny to fetch and download Java files
 * - Analyzes each file with FileAnalyzer
 * - Registers classes with the MetricsCalculator
 * - Computes metrics
 * - Updates the Blackboard and refreshes all UI components
 *
 * The heavy work is done in a background thread to keep the UI responsive.
 * @CAuthor Cory Cowden
 */

public class Listener implements ActionListener {
    private final JTextField pathField;
    private final Nanny nanny;
    private final GridPanel gridPanel;
    private final FilePanel filePanel;
    private final StatusBar statusBar;
    private final MetricsPanel metricsPanel;

    public Listener(JTextField pathField, Nanny nanny, GridPanel gridPanel, FilePanel filePanel, StatusBar statusBar, MetricsPanel metricsPanel) {
        this.pathField = pathField;
        this.nanny = nanny;
        this.gridPanel = gridPanel;
        this.filePanel = filePanel;
        this.statusBar = statusBar;
        this.metricsPanel = metricsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String url = pathField.getText().trim();
        if (url.isEmpty()) {
            Blackboard.getInstance().setStatus("Please enter a GitHub folder URL");
            statusBar.refresh();
            return;
        }

        Blackboard.getInstance().setStatus("Fetching files...");
        statusBar.refresh();

        new Thread(() -> {
            try {
                List<Nanny.FileEntry> entries = nanny.listFilesRecursiveFromGitHubFolderUrl(url);

                // clear previous metrics data
                MetricsCalculator.getInstance().clear();

                List<FileData> fileDataList = new ArrayList<>();
                int maxLines = 1;

                for (Nanny.FileEntry entry : entries) {
                    if (!entry.path.endsWith(".java")) continue;
                    try {
                        String content = nanny.getFileContentFromDownloadUrl(entry.downloadUrl);
                        FileData data = FileAnalyzer.analyze(entry.path, content);
                        fileDataList.add(data);
                        if (data.getLines() > maxLines) maxLines = data.getLines();

                        // register class using simple name (strip path & .java)
                        String simpleName = extractSimpleName(entry.path);
                        MetricsCalculator.getInstance().registerClass(simpleName, content);

                    } catch (IOException ex) {
                        System.out.println("Error reading " + entry.path + " : " + ex.getMessage());
                    }
                }

                Blackboard.getInstance().setFiles(fileDataList);
                Blackboard.getInstance().setStatus(fileDataList.size() + " files analyzed");

                // compute metrics
                MetricsCalculator.getInstance().computeMetrics();

                // update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    gridPanel.refresh();
                    filePanel.refresh();
                    statusBar.refresh();
                    metricsPanel.repaint();
                });

            } catch (IOException ex) {
                Blackboard.getInstance().setStatus("Error: " + ex.getMessage());
                SwingUtilities.invokeLater(statusBar::refresh);
            }
        }).start();
    }

    /** Extract the simple class name from a path like src/.../MyClass.java or package.name.MyClass.java */
    private String extractSimpleName(String pathOrName) {
        if (pathOrName == null) return "";
        String name = pathOrName;

        // if it's a file path, strip directory parts
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash != -1) {
            name = name.substring(slash + 1);
        }

        // if it's a dotted name, strip to last part
        int dot = name.lastIndexOf('.');
        if (dot != -1 && !name.endsWith(".java")) {
            // if dot exists but not a .java suffix, it might be package form; take last segment
            name = name.substring(dot + 1);
        }

        // remove .java if present
        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }

        return name;
    }
}
