package org.example;

import java.util.*;

/**
 * Central shared data model (Singleton) used by all GUI components.
 *
 * The Blackboard stores:
 * - The list of analyzed files
 * - The currently selected file name
 * - The status message for the status bar
 * - The computed metrics for each class
 *
 * All panels read from or write to this class, acting as a simple global
 * communication hub for the application.
 * @Author Cory Cowden, Xio Alcala
 */

public class Blackboard {
    private static Blackboard instance;
    private List<FileData> files;
    private String selectedFileName;
    private String status;
    private Map<String, Metric> metrics;

    private Blackboard() {
        files = new ArrayList<>();
        selectedFileName = "None";
        status = "Ready";
        metrics = new LinkedHashMap<>();
    }

    public static Blackboard getInstance() {
        if (instance == null) instance = new Blackboard();
        return instance;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void setSelectedFileName(String name) {
        this.selectedFileName = name;
    }

    public String getSelectedFileName() {
        return selectedFileName;
    }

    public void setStatus(String msg) {
        this.status = msg;
    }

    public String getStatus() {
        return status;
    }

    public void clear() {
        files.clear();
        selectedFileName = "None";
        status = "Cleared";
        metrics.clear();
    }

    public void addMetric(String name, Metric m) {
        metrics.put(name, m);
    }

    public Map<String, Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Metric> m) {
        this.metrics = m;
    }
}
