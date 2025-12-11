package org.example;

import java.util.*;

public class Relation {

    public final String from;   // source side of relationship
    public final String to;     // target side
    public final RelType type;  // relationship kind

    public Relation(String from, String to, RelType type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public enum RelType {
        EXTENDS,
        REALIZES,
        COMPOSITION,
        AGGREGATION,
        ASSOCIATION,
        DEPENDENCY
    }

    private static final List<RelType> PRECEDENCE = List.of(
            RelType.EXTENDS,
            RelType.REALIZES,
            RelType.COMPOSITION,
            RelType.AGGREGATION,
            RelType.ASSOCIATION,
            RelType.DEPENDENCY
    );

    public static void addRelation(Map<String, RelType> map,
                                   String from,
                                   String to,
                                   RelType newType) {

        String key = from + "|" + to;
        RelType old = map.get(key);

        if (old == null) {
            map.put(key, newType);
            return;
        }

        int oldRank = PRECEDENCE.indexOf(old);
        int newRank = PRECEDENCE.indexOf(newType);

        if (newRank < oldRank) {
            map.put(key, newType);
        }
    }
}
