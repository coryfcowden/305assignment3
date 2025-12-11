package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

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

        new Thread(() -> {
            try {
                List<Nanny.FileEntry> entries =
                        nanny.listFilesRecursiveFromGitHubFolderUrl(url);

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

                MetricsCalculator.getInstance().computeMetrics();

                String uml = UMLGenerator.generatePlantUML(classSources);

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


    private String extractSimpleName(String pathOrName) {

        if (pathOrName == null) return "";
        String name = pathOrName;

        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash != -1) {
            name = name.substring(slash + 1);
        }

        int dot = name.lastIndexOf('.');
        if (dot != -1 && !name.endsWith(".java")) {
            name = name.substring(dot + 1);
        }

        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }

        return name;
    }
}
