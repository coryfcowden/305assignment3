package org.example;

import java.util.*;

/**
 * High‑level coordinator for UML generation.
 *
 * Takes a map of simple class names → source code,
 * cleans the text, detects class types and relationships,
 * and finally asks PlantUMLBuilder to create the diagram text.
 */
public class UMLGenerator {

    private static final TypeDetector typeDetector = new TypeDetector();
    private static final RelationshipDetector relationshipDetector = new RelationshipDetector();
    private static final PlantUMLBuilder plantUmlBuilder = new PlantUMLBuilder();

    /**
     * Called by Listener → DiagramPanel.
     * Returns a full PlantUML diagram as plain text.
     */
    public static String generatePlantUML(Map<String, String> classSources) {

        if (classSources == null) {
            classSources = Collections.emptyMap();
        }

        // Clean up code (strip comments, standardize names)
        Map<String, String> cleaned = new LinkedHashMap<>();

        for (var entry : classSources.entrySet()) {
            String name = TypeUtils.extractSimpleName(entry.getKey());
            String code = entry.getValue() == null
                    ? ""
                    : TypeUtils.stripComments(entry.getValue());

            cleaned.put(name, code);
        }

        Set<String> classNames = cleaned.keySet();

        // Step 1: detect whether each file defines a class, abstract class, or interface
        Map<String, String> typeMap = typeDetector.detectTypes(cleaned);

        // Step 2: analyze fields, inheritance, params, local variables, etc.
        List<Relation> relations = relationshipDetector.detectRelations(cleaned, classNames);

        // Step 3: send everything to the PlantUML builder
        return plantUmlBuilder.build(typeMap, relations);
    }
}
