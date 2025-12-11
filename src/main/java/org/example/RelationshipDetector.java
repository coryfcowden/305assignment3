package org.example;

import java.util.*;

/**
 * Coordinates the three different relationship detectors
 * (inheritance, fields, and dependencies) and then produces
 * a combined list of Relation objects.
 *
 * Each detector fills the same map, and we later convert that
 * map into actual Relation instances.
 */
public class RelationshipDetector {

    private final InheritanceDetector inheritanceDetector = new InheritanceDetector();
    private final FieldRelationshipDetector fieldDetector = new FieldRelationshipDetector();
    private final DependencyDetector dependencyDetector = new DependencyDetector();

    /**
     * Runs all three detectors and returns the final list of relationships.
     */
    public List<Relation> detectRelations(Map<String, String> cleaned,
                                          Set<String> classNames) {

        // Stores strongest relationship per "A|B"
        Map<String, Relation.RelType> strongest = new LinkedHashMap<>();

        // Each detector adds entries into the same map
        inheritanceDetector.detect(cleaned, classNames, strongest);
        fieldDetector.detect(cleaned, classNames, strongest);
        dependencyDetector.detect(cleaned, classNames, strongest);

        // Convert "A|B → TYPE" into actual Relation objects
        List<Relation> result = new ArrayList<>();
        for (var entry : strongest.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            result.add(new Relation(parts[0], parts[1], entry.getValue()));
        }

        return result;
    }
}
