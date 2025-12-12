package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores raw class source content for later metric computation.
 * Normalizes class names into simple names and maintains an ordered
 * registry of class → source content mappings.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class MetricRegistry {

    private final Map<String, String> classContents = new LinkedHashMap<>();

    public void register(String className, String content) {
        if (className == null) return;

        String simple = extractSimpleName(className);
        classContents.put(simple, content == null ? "" : content);
    }

    public Map<String, String> getClassContents() {
        return classContents;
    }

    public void clear() {
        classContents.clear();
    }

    private String extractSimpleName(String name) {
        if (name == null) return "";

        String n = name;

        int slash = Math.max(n.lastIndexOf('/'), n.lastIndexOf('\\'));
        if (slash != -1) {
            n = n.substring(slash + 1);
        }

        if (n.contains(".")) {
            String[] parts = n.split("\\.");
            n = parts[parts.length - 1];
        }

        if (n.endsWith(".java")) {
            n = n.substring(0, n.length() - 5);
        }

        return n;
    }
}
