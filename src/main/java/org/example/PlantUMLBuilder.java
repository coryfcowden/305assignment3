package org.example;

import java.util.*;

public class PlantUMLBuilder {

    public String build(Map<String, String> typeDecls, List<Relation> relations) {

        StringBuilder sb = new StringBuilder();

        sb.append("@startuml\n");
        sb.append("!pragma layout smetana\n\n");

        // Write type declarations
        for (var e : typeDecls.entrySet()) {
            sb.append(e.getValue()).append(" ").append(e.getKey()).append("\n");
        }
        sb.append("\n");

        // Write relations
        for (Relation r : relations) {
            String arrow = switch (r.type) {
                case EXTENDS -> " --|> ";
                case REALIZES -> " ..|> ";
                case COMPOSITION -> " *-- ";
                case AGGREGATION -> " o-- ";
                case ASSOCIATION -> " --> ";
                case DEPENDENCY -> " ..> ";
            };

            sb.append(r.from).append(arrow).append(r.to).append("\n");
        }

        sb.append("\n@enduml");
        return sb.toString();
    }
}