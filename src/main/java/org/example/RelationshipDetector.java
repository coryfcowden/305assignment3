package org.example;

import java.util.*;

public class RelationshipDetector {

    private final InheritanceDetector inheritanceDetector = new InheritanceDetector();
    private final FieldRelationshipDetector fieldDetector = new FieldRelationshipDetector();
    private final DependencyDetector dependencyDetector = new DependencyDetector();

    /**
     * Detect UML relations from class source maps.
     */
    public List<Relation> detectRelations(Map<String, String> cleaned, Set<String> classNames) {

        Map<String, Relation.RelType> strongest = new LinkedHashMap<>();

        inheritanceDetector.detect(cleaned, classNames, strongest);
        fieldDetector.detect(cleaned, classNames, strongest);
        dependencyDetector.detect(cleaned, classNames, strongest);

        List<Relation> result = new ArrayList<>();
        for (var e : strongest.entrySet()) {
            String[] parts = e.getKey().split("\\|");
            result.add(new Relation(parts[0], parts[1], e.getValue()));
        }
        return result;
    }
}