package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Computes software metrics (Abstractness and Instability) for all loaded classes.
 *
 * This class is implemented as a singleton and stores the raw text contents of
 * each class. It analyzes how often classes reference each other to measure
 * incoming and outgoing dependencies, and detects abstract types based on
 * keywords such as "interface" or "abstract". The final metrics are saved into
 * the Blackboard for use by the GUI.
 * @Author Cory Cowden
 */


public class MetricsCalculator {
    private static MetricsCalculator instance;
    private final Map<String, String> classContents = new LinkedHashMap<>();

    private MetricsCalculator() {}

    public static MetricsCalculator getInstance() {
        if (instance == null) instance = new MetricsCalculator();
        return instance;
    }

    public void registerClass(String className, String content) {
        if (className == null) return;
        String simple = extractSimpleName(className);
        classContents.put(simple, content == null ? "" : content);
    }

    public void computeMetrics() {
        Map<String, Integer> incoming = new HashMap<>();
        Map<String, Integer> outgoing = new HashMap<>();

        for (String cls : classContents.keySet()) {
            incoming.put(cls, 0);
            outgoing.put(cls, 0);
        }

        for (String classA : classContents.keySet()) {
            String contentA = classContents.get(classA);
            if (contentA == null) contentA = "";

            for (String classB : classContents.keySet()) {
                if (classA.equals(classB)) continue;

                Pattern p = Pattern.compile("\\b" + Pattern.quote(classB) + "\\b");
                Matcher m = p.matcher(contentA);
                if (m.find()) {
                    outgoing.put(classA, outgoing.get(classA) + 1);
                    incoming.put(classB, incoming.get(classB) + 1);
                }
            }
        }

        Map<String, Metric> computed = new LinkedHashMap<>();

        for (String className : classContents.keySet()) {
            int in = incoming.getOrDefault(className, 0);
            int out = outgoing.getOrDefault(className, 0);
            double instability = (in + out) == 0 ? 0.0 : (double) out / (in + out);

            String content = classContents.getOrDefault(className, "");
            // detect abstractness: interface, abstract class, or annotation interface
            double abstractness = 0.0;
            String lower = content.toLowerCase();
            if (lower.contains("interface") || lower.contains("abstract") || lower.contains("@interface")) {
                abstractness = 1.0;
            }

            computed.put(className, new Metric(className, abstractness, instability));
        }

        Blackboard.getInstance().setMetrics(computed);
    }

    public void clear() {
        classContents.clear();
    }

    private String extractSimpleName(String name) {
        if (name == null) return "";
        String n = name;

        int slash = Math.max(n.lastIndexOf('/'), n.lastIndexOf('\\'));
        if (slash != -1) n = n.substring(slash + 1);

        if (n.contains(".")) {
            String[] parts = n.split("\\.");
            n = parts[parts.length - 1];
        }

        if (n.endsWith(".java")) n = n.substring(0, n.length() - 5);
        return n;
    }
}
