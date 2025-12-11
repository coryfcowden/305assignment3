package org.example;

import java.util.Map;

public class InstabilityCalculator {

    public double compute(String className,
                          Map<String, Integer> incoming,
                          Map<String, Integer> outgoing) {

        int in = incoming.getOrDefault(className, 0);
        int out = outgoing.getOrDefault(className, 0);

        if (in + out == 0) return 0.0;
        return (double) out / (in + out);
    }
}
