package org.example;

import java.util.*;
import java.util.regex.*;

/**
 * Looks for inheritance and interface implementation
 * in each class source file.
 *
 * This detector only checks for:
 *   - class A extends B
 *   - class A implements X, Y, Z
 *
 * Nothing else is changed from the original logic.
 */
public class InheritanceDetector {

    // match: class Child extends Parent
    private static final Pattern EXTENDS =
            Pattern.compile("\\bclass\\s+([A-Z][A-Za-z0-9_]*)\\s+extends\\s+([A-Za-z0-9_\\.<>]+)");

    // match: class A implements X, Y
    private static final Pattern IMPLEMENTS =
            Pattern.compile("\\bclass\\s+([A-Z][A-Za-z0-9_]*)\\s+implements\\s+([A-Za-z0-9_\\.,\\s<>]+)");

    /**
     * Detect EXTENDS and IMPLEMENTS relationships.
     * Adds results into the shared 'out' map.
     */
    public void detect(Map<String, String> cleaned,
                       Set<String> classNames,
                       Map<String, Relation.RelType> out) {

        for (Map.Entry<String, String> entry : cleaned.entrySet()) {
            String content = entry.getValue();

            // ---------------------------
            //           EXTENDS
            // ---------------------------
            Matcher extendsMatcher = EXTENDS.matcher(content);
            while (extendsMatcher.find()) {
                String child = extendsMatcher.group(1);
                String parent = TypeUtils.simpleFromQualified(extendsMatcher.group(2));

                // Only add if both appear in the project
                if (classNames.contains(child) && classNames.contains(parent)) {
                    Relation.addRelation(out, child, parent, Relation.RelType.EXTENDS);
                }
            }

            // ---------------------------
            //        IMPLEMENTS
            // ---------------------------
            Matcher implMatcher = IMPLEMENTS.matcher(content);
            while (implMatcher.find()) {
                String className = implMatcher.group(1);
                String list = implMatcher.group(2);

                // split interfaces: "X, Y, Z"
                for (String iface : list.split(",")) {
                    String simple = TypeUtils.simpleFromQualified(iface.trim());

                    if (classNames.contains(className) && classNames.contains(simple)) {
                        Relation.addRelation(out, className, simple, Relation.RelType.REALIZES);
                    }
                }
            }
        }
    }
}
