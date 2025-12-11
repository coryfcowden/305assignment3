package org.example;

import java.util.List;
import java.util.Map;

public class PlantUMLBuilder {
    public String build(Map<String, String> typeDecls, List<Relation> relations) {

        StringBuilder sb = new StringBuilder();

        sb.append("@startuml\n");
        sb.append("!pragma layout smetana\n\n");

        for (Map.Entry<String, String> entry : typeDecls.entrySet()) {
            String typeKeyword = entry.getValue();
            String name = entry.getKey();
            sb.append(typeKeyword).append(" ").append(name).append("\n");
        }

        sb.append("\n");

        for (Relation rel : relations) {
            String arrow;

            switch (rel.type) {
                case EXTENDS -> arrow = " --|> ";
                case REALIZES -> arrow = " ..|> ";
                case COMPOSITION -> arrow = " *-- ";
                case AGGREGATION -> arrow = " o-- ";
                case ASSOCIATION -> arrow = " --> ";
                default -> arrow = " ..> ";
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
