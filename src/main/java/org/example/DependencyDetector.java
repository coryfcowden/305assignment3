package org.example;

import java.util.*;
import java.util.regex.*;

/**
 * Detects structural dependencies between classes by scanning cleaned Java
 * source for object creation, local variables, and method parameter types.
 * Stores discovered relations in the provided output map.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class DependencyDetector {

    private static final Pattern NEW_EXPR =
            Pattern.compile("new\\s+([A-Z][A-Za-z0-9_]*)\\s*\\(");

    private static final Pattern LOCAL_VAR =
            Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\s+[a-zA-Z_][A-Za-z0-9_]*\\s*=");

    private static final Pattern METHOD_PARAM =
            Pattern.compile("\\(([^)]*)\\)");

    public void detect(Map<String, String> cleaned,
                       Set<String> classNames,
                       Map<String, Relation.RelType> out) {

        for (var entry : cleaned.entrySet()) {

            String cls = entry.getKey();
            String content = entry.getValue();

            Matcher mNew = NEW_EXPR.matcher(content);
            while (mNew.find()) {
                String type = mNew.group(1);
                if (classNames.contains(type)) {
                    Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                }
            }


            Matcher mLocal = LOCAL_VAR.matcher(content);
            while (mLocal.find()) {
                String type = mLocal.group(1);
                if (classNames.contains(type)) {
                    Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                }
            }


            Matcher mParams = METHOD_PARAM.matcher(content);
            while (mParams.find()) {

                String inside = mParams.group(1).trim();
                if (inside.isEmpty()) continue;

                String[] parts = inside.split(",");

                for (String part : parts) {
                    String[] tokens = part.trim().split("\\s+");
                    if (tokens.length == 0) continue;

                    String rawType = tokens[0];
                    String type = TypeUtils.extractPrimaryType(rawType);

                    if (classNames.contains(type)) {
                        Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                    }
                }
            }
        }
    }
}
