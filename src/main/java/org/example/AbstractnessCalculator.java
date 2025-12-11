package org.example;

public class AbstractnessCalculator {

    public double compute(String content) {
        if (content == null) return 0.0;
        String lower = content.toLowerCase();

        if (lower.contains("interface") ||
                lower.contains("abstract") ||
                lower.contains("@interface")) {
            return 1.0;
        }
        return 0.0;
    }
}
