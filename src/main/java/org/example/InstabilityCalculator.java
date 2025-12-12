package org.example;

import java.util.Map;

/**
 * Computes Instability metric (I = Ce / (Ca + Ce)),
 * using incoming and outgoing dependency counts for a given class.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */


public class InstabilityCalculator {

    public double compute(String className,
                          Map<String, Integer> incoming,
                          Map<String, Integer> outgoing) {

        int in = incoming.getOrDefault(className, 0);
        int out = outgoing.getOrDefault(className, 0);

        int total = in + out;
        if (total == 0) {
            return 0.0;
        }

        return (double) out / total;
    }
}
