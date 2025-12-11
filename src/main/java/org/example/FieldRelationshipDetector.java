package org.example;

import java.util.*;
import java.util.regex.*;


public class FieldRelationshipDetector {

    private static final Pattern FIELD =
            Pattern.compile("\\b(private|protected|public)\\s+([A-Za-z0-9_\\.<>, ?]+)\\s+([A-Za-z0-9_]+)\\s*;");

    private static final Set<String> COLLECTIONS =
            Set.of("List", "Set", "Map", "Collection");


    public void detect(Map<String, String> cleaned,
                       Set<String> classNames,
                       Map<String, Relation.RelType> out) {

        for (var entry : cleaned.entrySet()) {

            String cls = entry.getKey();
            String content = entry.getValue();

            Matcher matcher = FIELD.matcher(content);

            while (matcher.find()) {

                String visibility = matcher.group(1);
                String rawType = matcher.group(2);

                String type = TypeUtils.extractPrimaryType(rawType);

                if (!classNames.contains(type)) {
                    continue;
                }

                boolean isCollection = false;
                for (String col : COLLECTIONS) {
                    if (rawType.contains(col)) {
                        isCollection = true;
                        break;
                    }
                }

                if (visibility.equals("private") && !isCollection) {
                    Relation.addRelation(out, cls, type, Relation.RelType.COMPOSITION);
                }
                else if (!isCollection && (visibility.equals("protected") || visibility.equals("public"))) {
                    Relation.addRelation(out, cls, type, Relation.RelType.AGGREGATION);
                }
                else {
                    Relation.addRelation(out, cls, type, Relation.RelType.ASSOCIATION);
                }
            }
        }
    }
}
