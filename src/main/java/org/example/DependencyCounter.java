package org.example;

import java.util.*;
import java.util.regex.*;

/**
 * Counts simple incoming and outgoing dependencies between classes
 * based on name references inside class content.
 *
 * This version keeps the same logic but is written in a simpler,
 * more student‑like style.
 */
public class DependencyCounter {

    /**
     * Count how many times each class is referenced BY other classes.
     * incoming[B]++ means A → B
     */
    public Map<String, Integer> computeIncoming(Map<String, String> classes) {
        Map<String, Integer> incoming = new HashMap<>();

        // initialize all counts
        for (String cls : classes.keySet()) {
            incoming.put(cls, 0);
        }

        // look at every class and see which names it mentions
        for (String from : classes.keySet()) {
            String content = classes.get(from);

            for (String to : classes.keySet()) {
                if (from.equals(to)) continue;

                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(to) + "\\b");
                Matcher matcher = pattern.matcher(content);

                if (matcher.find()) {
                    incoming.put(to, incoming.get(to) + 1);
                }
            }
        }

        return incoming;
    }

    /**
     * Count how many classes each class references.
     * outgoing[A]++ means A → B
     */
    public Map<String, Integer> computeOutgoing(Map<String, String> classes) {
        Map<String, Integer> outgoing = new HashMap<>();

        // initialize all counts
        for (String cls : classes.keySet()) {
            outgoing.put(cls, 0);
        }

        // check if a class mentions another class name
        for (String from : classes.keySet()) {
            String content = classes.get(from);

            for (String to : classes.keySet()) {
                if (from.equals(to)) continue;

                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(to) + "\\b");
                Matcher matcher = pattern.matcher(content);

                if (matcher.find()) {
                    outgoing.put(from, outgoing.get(from) + 1);
                }
            }
        }

        return outgoing;
    }
}
