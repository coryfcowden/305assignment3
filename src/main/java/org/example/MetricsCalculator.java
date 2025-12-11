package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Coordinates the computation of all metrics for the project.
 *
 * This class pulls together:
 *  - The registry of raw class contents
 *  - Dependency counting
 *  - Abstractness calculation
 *  - Instability calculation
 *
 * Results are written to the Blackboard so the GUI can display them.
 */
public class MetricsCalculator {

    private static MetricsCalculator instance;

    private final MetricRegistry registry = new MetricRegistry();
    private final DependencyCounter depCounter = new DependencyCounter();
    private final AbstractnessCalculator absCalc = new AbstractnessCalculator();
    private final InstabilityCalculator instCalc = new InstabilityCalculator();

    private MetricsCalculator() {}

    public static MetricsCalculator getInstance() {
        if (instance == null) {
            instance = new MetricsCalculator();
        }
        return instance;
    }

    /**
     * Store a class and its source code for later analysis.
     */
    public void registerClass(String className, String content) {
        registry.register(className, content);
    }

    /**
     * Computes Abstractness + Instability for each class and saves the results in the Blackboard.
     */
    public void computeMetrics() {
        Map<String, String> classes = registry.getClassContents();

        // Count incoming and outgoing references between all classes
        Map<String, Integer> incoming = depCounter.computeIncoming(classes);
        Map<String, Integer> outgoing = depCounter.computeOutgoing(classes);

        Map<String, Metric> results = new LinkedHashMap<>();

        for (String cls : classes.keySet()) {
            String content = classes.get(cls);

            double abstractness = absCalc.compute(content);
            double instability = instCalc.compute(cls, incoming, outgoing);

            results.put(cls, new Metric(cls, abstractness, instability));
        }

        Blackboard.getInstance().setMetrics(results);
    }

    /**
     * Clears all stored class data.
     */
    public void clear() {
        registry.clear();
    }
}
