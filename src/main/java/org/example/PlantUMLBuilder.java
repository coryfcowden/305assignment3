package org.example;

import java.util.List;
import java.util.Map;

/**
 * Builds the final PlantUML text from detected types and relationships.
 * This class is intentionally very simple — it just turns our internal
 * representation into PlantUML syntax.
 */
public class PlantUMLBuilder {

    /**
     * Generate the PlantUML diagram text.
     *
     * @param typeDecls Map of className -> "class"/"interface"/"abstract class"
     * @param relations List of relationships between classes
     */
    public String build(Map<String, String> typeDecls, List<Relation> relations) {

        StringBuilder sb = new StringBuilder();

        sb.append("@startuml\n");
        sb.append("!pragma layout smetana\n\n");

        // Add class/interface/abstract declarations
        for (Map.Entry<String, String> entry : typeDecls.entrySet()) {
            String typeKeyword = entry.getValue();
            String name = entry.getKey();
            sb.append(typeKeyword).append(" ").append(name).append("\n");
        }

        sb.append("\n");

        // Add the arrows between classes
        for (Relation rel : relations) {
            String arrow;

            // Pick the UML arrow type based on the relationship
            switch (rel.type) {
                case EXTENDS:
                    arrow = " --|> ";
                    break;
                case REALIZES:
                    arrow = " ..|> ";
                    break;
                case COMPOSITION:
                    arrow = " *-- ";
                    break;
                case AGGREGATION:
                    arrow = " o-- ";
                    break;
                case ASSOCIATION:
                    arrow = " --> ";
                    break;
                case DEPENDENCY:
                default:
                    arrow = " ..> ";
            }

            sb.append(rel.from)
                    .append(arrow)
                    .append(rel.to)
                    .append("\n");
        }

        sb.append("\n@enduml");
        return sb.toString();
    }
}
