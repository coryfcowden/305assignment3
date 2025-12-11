package org.example;

import java.util.*;
import java.util.regex.*;

public class DependencyCounter {


    public Map<String, Integer> computeIncoming(Map<String, String> classes) {
        Map<String, Integer> incoming = new HashMap<>();

        for (String cls : classes.keySet()) {
            incoming.put(cls, 0);
        }

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

    public Map<String, Integer> computeOutgoing(Map<String, String> classes) {
        Map<String, Integer> outgoing = new HashMap<>();

        for (String cls : classes.keySet()) {
            outgoing.put(cls, 0);
        }

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
