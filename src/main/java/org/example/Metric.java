package org.example;

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