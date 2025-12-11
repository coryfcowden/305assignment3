package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

/**
 * Handles the click of the "OK" button.
 * Fetches the files, analyzes them, computes metrics,
 * and sends the UML source to the diagram panel.
 */
public class Listener implements ActionListener {

    private final JTextField pathField;
    private final Nanny nanny;
    private final GridPanel gridPanel;
    private final FilePanel filePanel;
    private final StatusBar statusBar;
    private final MetricsPanel metricsPanel;
    private final DiagramPanel diagramPanel;

    public Listener(JTextField pathField,
                    Nanny nanny,
                    GridPanel gridPanel,
                    FilePanel filePanel,
                    StatusBar statusBar,
                    MetricsPanel metricsPanel,
                    DiagramPanel diagramPanel) {

        this.pathField = pathField;
        this.nanny = nanny;
        this.gridPanel = gridPanel;
        this.filePanel = filePanel;
        this.statusBar = statusBar;
        this.metricsPanel = metricsPanel;
        this.diagramPanel = diagramPanel;
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

        // run fetch / analysis in a background thread
        new Thread(() -> {
            try {
                List<Nanny.FileEntry> entries =
                        nanny.listFilesRecursiveFromGitHubFolderUrl(url);

                // reset metrics for this run
                MetricsCalculator.getInstance().clear();

                List<FileData> fileDataList = new ArrayList<>();
                Map<String, String> classSources = new LinkedHashMap<>();

                for (Nanny.FileEntry entry : entries) {
                    if (!entry.path.endsWith(".java")) {
                        continue;
                    }

                    try {
                        String content = nanny.getFileContentFromDownloadUrl(entry.downloadUrl);

                        FileData data = FileAnalyzer.analyze(entry.path, content);
                        fileDataList.add(data);

                        String simpleName = extractSimpleName(entry.path);
                        MetricsCalculator.getInstance().registerClass(simpleName, content);

                        classSources.put(simpleName, content);

                    } catch (IOException ex) {
                        System.out.println("Error reading " + entry.path + ": " + ex.getMessage());
                    }
                }

                Blackboard.getInstance().setFiles(fileDataList);
                Blackboard.getInstance().setStatus(fileDataList.size() + " files analyzed");

                // compute metrics for the metrics panel
                MetricsCalculator.getInstance().computeMetrics();

                // build UML diagram source
                String uml = UMLGenerator.generatePlantUML(classSources);

                // update GUI on the Swing thread
                SwingUtilities.invokeLater(() -> {
                    gridPanel.refresh();
                    filePanel.refresh();
                    statusBar.refresh();
                    metricsPanel.repaint();
                    diagramPanel.setUml(uml);
                });

            } catch (IOException ex) {
                Blackboard.getInstance().setStatus("Error: " + ex.getMessage());
                SwingUtilities.invokeLater(statusBar::refresh);
            }
        }).start();
    }

    /**
     * Extracts the simple name from a file path or qualified name.
     */
    private String extractSimpleName(String pathOrName) {

        if (pathOrName == null) return "";
        String name = pathOrName;

        // remove directory portion
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash != -1) {
            name = name.substring(slash + 1);
        }

        // remove trailing package portion if any
        int dot = name.lastIndexOf('.');
        if (dot != -1 && !name.endsWith(".java")) {
            name = name.substring(dot + 1);
        }

        // strip .java extension
        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }

        return name;
    }
}
