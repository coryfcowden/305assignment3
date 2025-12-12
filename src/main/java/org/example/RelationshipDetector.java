package org.example;

import java.util.*;

/**
 * Coordinates all detectors (inheritance, field-based, and dependency)
 * to derive the strongest relationship between classes. Produces a
 * list of Relation objects for UML generation.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */


public class RelationshipDetector {

    private final InheritanceDetector inheritanceDetector = new InheritanceDetector();
    private final FieldRelationshipDetector fieldDetector = new FieldRelationshipDetector();
    private final DependencyDetector dependencyDetector = new DependencyDetector();

    public List<Relation> detectRelations(Map<String, String> cleaned,
                                          Set<String> classNames) {

        Map<String, Relation.RelType> strongest = new LinkedHashMap<>();

        inheritanceDetector.detect(cleaned, classNames, strongest);
        fieldDetector.detect(cleaned, classNames, strongest);
        dependencyDetector.detect(cleaned, classNames, strongest);

        List<Relation> result = new ArrayList<>();
        for (var entry : strongest.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            result.add(new Relation(parts[0], parts[1], entry.getValue()));
        }

        return result;
    }
}
