package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Figures out whether each source file declares a normal class,
 * an interface, or an abstract class.
 *
 * Uses simple regex checks — not a full Java parser.
 */
public class TypeDetector {

    // Match "class Foo" or "interface Bar"
    private static final Pattern CLASS_PATTERN =
            Pattern.compile("\\b(class|interface)\\s+([A-Z][A-Za-z0-9_]*)");

    // Match "abstract class Foo"
    private static final Pattern ABSTRACT_PATTERN =
            Pattern.compile("\\babstract\\s+class\\s+([A-Z][A-Za-z0-9_]*)");

    /**
     * Returns a map: className → "class", "interface", or "abstract class".
     */
    public Map<String, String> detectTypes(Map<String, String> cleanedSources) {

        Map<String, String> types = new LinkedHashMap<>();

        for (String className : cleanedSources.keySet()) {

            String code = cleanedSources.get(className);

            // Check for abstract class first
            Matcher abstractMatch = ABSTRACT_PATTERN.matcher(code);
            if (abstractMatch.find() && abstractMatch.group(1).equals(className)) {
                types.put(className, "abstract class");
                continue;
            }

            // Otherwise check for class or interface
            Matcher classMatch = CLASS_PATTERN.matcher(code);
            boolean matched = false;

            while (classMatch.find()) {
                String kind = classMatch.group(1);   // class / interface
                String foundName = classMatch.group(2);

                if (foundName.equals(className)) {
                    types.put(className, kind);     // store "class" or "interface"
                    matched = true;
                    break;
                }
            }

            // Default fallback
            if (!matched) {
                types.put(className, "class");
            }
        }

        return types;
    }
}
