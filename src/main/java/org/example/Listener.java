package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;


public class Listener implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

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
        logger.info("OK button clicked with URL: '{}'", url);

        if (url.isEmpty()) {
            logger.warn("No URL entered by user.");
            Blackboard.getInstance().setStatus("Please enter a GitHub folder URL");
            statusBar.refresh();
            return;
        }

        Blackboard.getInstance().setStatus("Fetching files...");
        statusBar.refresh();
        logger.info("Starting background fetch for URL: {}", url);

        new Thread(() -> {
            try {
                List<Nanny.FileEntry> entries =
                        nanny.listFilesRecursiveFromGitHubFolderUrl(url);
                logger.info("Fetched {} entries from GitHub.", entries.size());

                MetricsCalculator.getInstance().clear();
                logger.debug("MetricsCalculator cleared.");

                List<FileData> fileDataList = new ArrayList<>();
                Map<String, String> classSources = new LinkedHashMap<>();

                for (Nanny.FileEntry entry : entries) {
                    if (!entry.path.endsWith(".java")) {
                        logger.debug("Skipping non-Java file: {}", entry.path);
                        continue;
                    }
                    try {
                        String content = nanny.getFileContentFromDownloadUrl(entry.downloadUrl);
                        FileData data = FileAnalyzer.analyze(entry.path, content);
                        fileDataList.add(data);

                        String simpleName = extractSimpleName(entry.path);
                        MetricsCalculator.getInstance().registerClass(simpleName, content);
                        classSources.put(simpleName, content);

                        logger.debug("Processed Java file: {}", entry.path);
                    } catch (IOException ex) {
                        logger.error("Error reading {}: {}", entry.path, ex.getMessage(), ex);
                        System.out.println("Error reading " + entry.path + ": " + ex.getMessage());
                    }
                }

                Blackboard.getInstance().setFiles(fileDataList);
                Blackboard.getInstance().setStatus(fileDataList.size() + " files analyzed");
                logger.info("Analysis complete. {} Java files analyzed.", fileDataList.size());

                MetricsCalculator.getInstance().computeMetrics();
                logger.info("Metrics computation finished.");

                String uml = UMLGenerator.generatePlantUML(classSources);
                logger.info("UML generation finished.");

                SwingUtilities.invokeLater(() -> {
                    gridPanel.refresh();
                    filePanel.refresh();
                    statusBar.refresh();
                    metricsPanel.repaint();
                    diagramPanel.setUml(uml);
                    logger.debug("GUI components refreshed after analysis.");
                });

            } catch (IOException ex) {
                logger.error("Failed to fetch files from GitHub: {}", ex.getMessage(), ex);
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
