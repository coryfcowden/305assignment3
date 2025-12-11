package org.example;

import java.util.*;

/**
 * Represents a UML relationship between two classes, and provides helpers
 * to manage precedence rules between relationship types.
 *
 * This combines both the Relation model and the utilities for manipulating
 * relationship maps (formerly RelationUtils).
 */
public class Relation {

    public final String from;     // source class
    public final String to;       // target class
    public final RelType type;    // relationship type

    public Relation(String from, String to, RelType type) {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    /**
     * Enum of all UML relationship types supported.
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
     * Precedence list: lower index = stronger relationship.
     * EXTENDS is strongest; DEPENDENCY is weakest.
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
     * Adds a relationship to the map, replacing weaker ones with stronger ones.
     *
     *  Example key: "Car|Engine"
     */
    public static void addRelation(Map<String, RelType> map,
                                   String from, String to, RelType newType) {

        String key = from + "|" + to;
        RelType existing = map.get(key);

        if (existing == null) {
            map.put(key, newType);
            return;
        }

        // Replace if newType is stronger (lower index)
        int existingIndex = PRECEDENCE.indexOf(existing);
        int newIndex = PRECEDENCE.indexOf(newType);

        if (newIndex < existingIndex) {
            map.put(key, newType);
        }
    }
}