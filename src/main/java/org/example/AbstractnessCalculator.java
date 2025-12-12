package org.example;

/**
 * Computes the abstractness metric of a class by checking for abstract or
 * interface keywords inside the source content. Returns a simple 0/1 score.
 *
 * @author Xiomara Alcala
 * @author Cory Cowden
 */

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
