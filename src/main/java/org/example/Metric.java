package org.example;

/**
 * Holds the Abstractness and Instability values for a single class.
 *
 * These metrics are later plotted in the MetricsPanel. A convenience method
 * computes the class's distance from the "main sequence" line (A + I = 1),
 * used for quick quality assessment.
 * @Author Cory Cowden
 */

public class Metric {
    private final String className;
    private final double abstractness;
    private final double instability;

    public Metric(String className, double abstractness, double instability) {
        this.className = className;
        this.abstractness = abstractness;
        this.instability = instability;
    }

    public String getClassName() {
        return className;
    }

    public double getAbstractness() {
        return abstractness;
    }

    public double getInstability() {
        return instability;
    }

    public double distance() {
        return Math.abs(instability + abstractness - 1.0);
    }
}