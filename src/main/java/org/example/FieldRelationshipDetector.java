package org.example;

import java.util.*;
import java.util.regex.*;
//
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

            Matcher m = FIELD.matcher(content);

            while (m.find()) {
                String visibility = m.group(1);
                String raw = m.group(2);

                String type = TypeUtils.extractPrimaryType(raw);
                if (!classNames.contains(type)) continue;

                boolean isCollection = COLLECTIONS.stream().anyMatch(raw::contains);

                if ("private".equals(visibility) && !isCollection) {
                    Relation.addRelation(out, cls, type, Relation.RelType.COMPOSITION);
                } else if (!isCollection && (visibility.equals("protected") || visibility.equals("public"))) {
                    Relation.addRelation(out, cls, type, Relation.RelType.AGGREGATION);
                } else {
                    Relation.addRelation(out, cls, type, Relation.RelType.ASSOCIATION);
                }
            }
        }
    }
}