package org.example;

import java.util.*;

public class UMLGenerator {

    private static final TypeDetector typeDetector = new TypeDetector();
    private static final RelationshipDetector relationshipDetector = new RelationshipDetector();
    private static final PlantUMLBuilder plantUmlBuilder = new PlantUMLBuilder();

    /**
     *  Main entry point used by Listener → DiagramPanel.
     * Accepts a map: simpleClassName → sourceCode
     */
    public static String generatePlantUML(Map<String, String> classSources) {

        if (classSources == null) classSources = Collections.emptyMap();

        // Clean + normalize names
        Map<String, String> cleaned = new LinkedHashMap<>();
        for (var entry : classSources.entrySet()) {
            String simple = TypeUtils.extractSimpleName(entry.getKey());
            String code = entry.getValue() == null ? "" : TypeUtils.stripComments(entry.getValue());
            cleaned.put(simple, code);
        }

        Set<String> classNames = cleaned.keySet();

        // Determine types (class / interface / abstract)
        Map<String, String> typeDeclarations = typeDetector.detectTypes(cleaned);

        // Detect UML relationships
        List<Relation> relations = relationshipDetector.detectRelations(cleaned, classNames);

        // Build final PlantUML diagram
        return plantUmlBuilder.build(typeDeclarations, relations);
    }
}