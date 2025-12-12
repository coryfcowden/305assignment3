package org.example;

/**
 * Represents a single computed metric for a class, containing its
 * abstractness, instability, and a helper method for distance from
 * the main-sequence line (A + I = 1).
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
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