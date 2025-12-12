package org.example;

import java.util.*;

/**
 * High-level orchestrator that generates a complete PlantUML diagram.
 * Cleans sources, detects types, extracts relationships, and delegates
 * final rendering to PlantUMLBuilder.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class UMLGenerator {

    private static final TypeDetector typeDetector = new TypeDetector();
    private static final RelationshipDetector relationshipDetector = new RelationshipDetector();
    private static final PlantUMLBuilder plantUmlBuilder = new PlantUMLBuilder();

    public static String generatePlantUML(Map<String, String> classSources) {

        if (classSources == null) {
            classSources = Collections.emptyMap();
        }

        Map<String, String> cleaned = new LinkedHashMap<>();

        for (var entry : classSources.entrySet()) {
            String name = TypeUtils.extractSimpleName(entry.getKey());
            String code = entry.getValue() == null
                    ? ""
                    : TypeUtils.stripComments(entry.getValue());

            cleaned.put(name, code);
        }

        Set<String> classNames = cleaned.keySet();

        Map<String, String> typeMap = typeDetector.detectTypes(cleaned);

        List<Relation> relations = relationshipDetector.detectRelations(cleaned, classNames);

        return plantUmlBuilder.build(typeMap, relations);
    }
}
