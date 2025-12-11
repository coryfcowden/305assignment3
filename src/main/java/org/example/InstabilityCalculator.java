package org.example;

import java.util.Map;

/**
 * Calculates instability for a given class using:
 *
 *   I = outgoing / (incoming + outgoing)
 *
 * Instability ranges from 0 (very stable) to 1 (very unstable).
 */
public class InstabilityCalculator {

    /**
     * Compute the instability value for a class.
     *
     * @param className the class to measure
     * @param incoming  map of class → incoming dependency count
     * @param outgoing  map of class → outgoing dependency count
     * @return instability value in [0,1]
     */
    public double compute(String className,
                          Map<String, Integer> incoming,
                          Map<String, Integer> outgoing) {

        int in = incoming.getOrDefault(className, 0);
        int out = outgoing.getOrDefault(className, 0);

        int total = in + out;
        if (total == 0) {
            // No dependencies means the class is fully stable in our model
            return 0.0;
        }

        return (double) out / total;
    }
}
