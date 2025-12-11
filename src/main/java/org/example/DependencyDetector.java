package org.example;

import java.util.*;
import java.util.regex.*;

/**
 * Detects simple dependency relationships using:
 *  - new X()
 *  - local variables (X var = ...)
 *  - method parameters
 *
 * Same functionality as before but written in a more natural, student‑like style.
 */
public class DependencyDetector {

    // look for: new ClassName(...)
    private static final Pattern NEW_EXPR =
            Pattern.compile("new\\s+([A-Z][A-Za-z0-9_]*)\\s*\\(");

    // look for: ClassName var =
    private static final Pattern LOCAL_VAR =
            Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\s+[a-zA-Z_][A-Za-z0-9_]*\\s*=");

    // method parameters: inside (...)
    private static final Pattern METHOD_PARAM =
            Pattern.compile("\\(([^)]*)\\)");

    /**
     * Detects dependency relationships between classes by scanning for:
     *   - constructor calls
     *   - variable declarations
     *   - method parameter types
     */
    public void detect(Map<String, String> cleaned,
                       Set<String> classNames,
                       Map<String, Relation.RelType> out) {

        for (var entry : cleaned.entrySet()) {

            String cls = entry.getKey();
            String content = entry.getValue();

            // ---------------------------------------------------------
            // 1) new X()
            // ---------------------------------------------------------
            Matcher mNew = NEW_EXPR.matcher(content);
            while (mNew.find()) {
                String type = mNew.group(1);
                if (classNames.contains(type)) {
                    Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                }
            }

            // ---------------------------------------------------------
            // 2) Local variable declarations: X name =
            // ---------------------------------------------------------
            Matcher mLocal = LOCAL_VAR.matcher(content);
            while (mLocal.find()) {
                String type = mLocal.group(1);
                if (classNames.contains(type)) {
                    Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                }
            }

            // ---------------------------------------------------------
            // 3) Method parameters: (Type param, Other thing, ...)
            // ---------------------------------------------------------
            Matcher mParams = METHOD_PARAM.matcher(content);
            while (mParams.find()) {

                String inside = mParams.group(1).trim();
                if (inside.isEmpty()) continue;

                // split multiple params
                String[] parts = inside.split(",");

                for (String part : parts) {
                    String[] tokens = part.trim().split("\\s+");
                    if (tokens.length == 0) continue;

                    // first token is usually the type
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
