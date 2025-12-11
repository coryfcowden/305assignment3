package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public void registerClass(String className, String content) {
        registry.register(className, content);
    }

    public void computeMetrics() {
        Map<String, String> classes = registry.getClassContents();

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

    public void clear() {
        registry.clear();
    }
}
