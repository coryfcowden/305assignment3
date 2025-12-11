package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores raw class contents for use by the metrics subsystem.
 * Each entry is kept under its simple class name.
 */
public class MetricRegistry {

    private final Map<String, String> classContents = new LinkedHashMap<>();

    /**
     * Register a class and its source code.
     */
    public void register(String className, String content) {
        if (className == null) return;

        String simple = extractSimpleName(className);
        classContents.put(simple, content == null ? "" : content);
    }

    /**
     * Get a read-only view of stored class contents.
     */
    public Map<String, String> getClassContents() {
        return classContents;
    }

    /**
     * Clear all stored data.
     */
    public void clear() {
        classContents.clear();
    }

    /**
     * Convert a file path or dotted name into a simple class name.
     */
    private String extractSimpleName(String name) {
        if (name == null) return "";

        String n = name;

        // Strip directory prefix if present.
        int slash = Math.max(n.lastIndexOf('/'), n.lastIndexOf('\\'));
        if (slash != -1) {
            n = n.substring(slash + 1);
        }

        // If there's a dot, take the last portion.
        if (n.contains(".")) {
            String[] parts = n.split("\\.");
            n = parts[parts.length - 1];
        }

        // Remove .java if present.
        if (n.endsWith(".java")) {
            n = n.substring(0, n.length() - 5);
        }

        return n;
    }
}
