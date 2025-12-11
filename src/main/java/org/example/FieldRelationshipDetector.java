package org.example;

import java.util.*;
import java.util.regex.*;

/**
 * Detects UML relationships that come from fields inside a class,
 * such as composition, aggregation, or basic association.
 *
 * This version keeps the exact logic but is written in a simpler,
 * more student‑friendly style.
 */
public class FieldRelationshipDetector {

    // Matches: visibility  type   name;
    private static final Pattern FIELD =
            Pattern.compile("\\b(private|protected|public)\\s+([A-Za-z0-9_\\.<>, ?]+)\\s+([A-Za-z0-9_]+)\\s*;");

    // A few collection types that should count as associations instead of ownership
    private static final Set<String> COLLECTIONS =
            Set.of("List", "Set", "Map", "Collection");

    /**
     * For each class, look at its fields and determine:
     *  - Composition: private non‑collection object
     *  - Aggregation: public/protected non‑collection object
     *  - Association: collections or anything else
     */
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

                // Grab the actual type (handles generics, packages, etc.)
                String type = TypeUtils.extractPrimaryType(rawType);

                // Ignore if the field type isn't one of the project classes
                if (!classNames.contains(type)) {
                    continue;
                }

                // Check if this is some kind of list/collection
                boolean isCollection = false;
                for (String col : COLLECTIONS) {
                    if (rawType.contains(col)) {
                        isCollection = true;
                        break;
                    }
                }

                // Apply your relationship rules
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
