package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects class, interface, and abstract class declarations inside
 * cleaned Java source code. Returns a map of className → type keyword.
 *
 * @author Cory Cowden
 * @author Xiomara Alcala
 */

public class TypeDetector {

    private static final Pattern CLASS_PATTERN =
            Pattern.compile("\\b(class|interface)\\s+([A-Z][A-Za-z0-9_]*)");

    private static final Pattern ABSTRACT_PATTERN =
            Pattern.compile("\\babstract\\s+class\\s+([A-Z][A-Za-z0-9_]*)");

    public Map<String, String> detectTypes(Map<String, String> cleanedSources) {

        Map<String, String> types = new LinkedHashMap<>();

        for (String className : cleanedSources.keySet()) {

            String code = cleanedSources.get(className);

            Matcher abstractMatch = ABSTRACT_PATTERN.matcher(code);
            if (abstractMatch.find() && abstractMatch.group(1).equals(className)) {
                types.put(className, "abstract class");
                continue;
            }

            Matcher classMatch = CLASS_PATTERN.matcher(code);
            boolean matched = false;

            while (classMatch.find()) {
                String kind = classMatch.group(1);
                String foundName = classMatch.group(2);

                if (foundName.equals(className)) {
                    types.put(className, kind);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                types.put(className, "class");
            }
        }

        return types;
    }
}
