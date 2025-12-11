package org.example;
//
import java.util.*;
import java.util.regex.*;

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

            // new X()
            Matcher mNew = NEW_EXPR.matcher(content);
            while (mNew.find()) {
                String type = mNew.group(1);
                if (classNames.contains(type)) {
                    Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                }
            }

            // local variable declarations
            Matcher mLocal = LOCAL_VAR.matcher(content);
            while (mLocal.find()) {
                String type = mLocal.group(1);
                if (classNames.contains(type)) {
                    Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                }
            }

            // method parameters
            Matcher mParam = METHOD_PARAM.matcher(content);
            while (mParam.find()) {
                String block = mParam.group(1);
                if (block.isBlank()) continue;

                for (String part : block.split(",")) {
                    String[] tokens = part.trim().split("\\s+");
                    if (tokens.length == 0) continue;

                    String type = TypeUtils.extractPrimaryType(tokens[0]);
                    if (classNames.contains(type)) {
                        Relation.addRelation(out, cls, type, Relation.RelType.DEPENDENCY);
                    }
                }
            }
        }
    }
}