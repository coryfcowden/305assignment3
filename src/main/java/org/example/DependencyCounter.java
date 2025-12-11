package org.example;

import java.util.*;
import java.util.regex.*;

public class DependencyCounter {

    public Map<String, Integer> computeIncoming(Map<String, String> classes) {
        Map<String, Integer> incoming = new HashMap<>();
        Map<String, Integer> outgoing = computeOutgoing(classes);

        for (String cls : classes.keySet()) {
            incoming.put(cls, 0);
        }

        for (String classA : classes.keySet()) {
            String contentA = classes.get(classA);
            for (String classB : classes.keySet()) {
                if (classA.equals(classB)) continue;

                Pattern p = Pattern.compile("\\b" + Pattern.quote(classB) + "\\b");
                Matcher m = p.matcher(contentA);

                if (m.find()) {
                    incoming.put(classB, incoming.get(classB) + 1);
                }
            }
        }
        return incoming;
    }

    public Map<String, Integer> computeOutgoing(Map<String, String> classes) {
        Map<String, Integer> outgoing = new HashMap<>();

        for (String cls : classes.keySet()) {
            outgoing.put(cls, 0);
        }

        for (String classA : classes.keySet()) {
            String contentA = classes.get(classA);

            for (String classB : classes.keySet()) {
                if (classA.equals(classB)) continue;

                Pattern p = Pattern.compile("\\b" + Pattern.quote(classB) + "\\b");
                Matcher m = p.matcher(contentA);

                if (m.find()) {
                    outgoing.put(classA, outgoing.get(classA) + 1);
                }
            }
        }
        return outgoing;
    }
}
