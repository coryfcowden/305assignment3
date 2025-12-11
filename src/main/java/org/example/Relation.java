package org.example;

import java.util.*;

/**
 * Simple class representing a UML relationship between two classes.
 * Also includes small helper logic for choosing the strongest relationship type
 * when multiple types appear between the same pair of classes.
 */
public class Relation {

    public final String from;   // source side of relationship
    public final String to;     // target side
    public final RelType type;  // relationship kind

    public Relation(String from, String to, RelType type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    /**
     * Types of relationships we care about in our simplified UML generator.
     */
    public enum RelType {
        EXTENDS,
        REALIZES,
        COMPOSITION,
        AGGREGATION,
        ASSOCIATION,
        DEPENDENCY
    }

    /**
     * Order of strength. Earlier means “stronger.”
     * (Example: composition should override association.)
     */
    private static final List<RelType> PRECEDENCE = List.of(
            RelType.EXTENDS,
            RelType.REALIZES,
            RelType.COMPOSITION,
            RelType.AGGREGATION,
            RelType.ASSOCIATION,
            RelType.DEPENDENCY
    );

    /**
     * Adds or updates a relationship in the map. If one already exists,
     * we only replace it when the new relationship is considered stronger.
     */
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

        // Compare strength using the list order.
        int oldRank = PRECEDENCE.indexOf(old);
        int newRank = PRECEDENCE.indexOf(newType);

        if (newRank < oldRank) {
            map.put(key, newType);
        }
    }
}
