package org.example;

import java.util.*;
import java.util.regex.*;

public class TypeDetector {

    private static final Pattern CLASS_PATTERN =
            Pattern.compile("\\b(class|interface)\\s+([A-Z][A-Za-z0-9_]*)");

    private static final Pattern ABSTRACT_PATTERN =
            Pattern.compile("\\babstract\\s+class\\s+([A-Z][A-Za-z0-9_]*)");

    /**
     * Detects class type: class, interface, or abstract class.
     */
    public Map<String, String> detectTypes(Map<String, String> cleaned) {

        Map<String, String> result = new LinkedHashMap<>();

        for (String cls : cleaned.keySet()) {
            String content = cleaned.get(cls);

            // Abstract?
            Matcher ma = ABSTRACT_PATTERN.matcher(content);
            if (ma.find() && ma.group(1).equals(cls)) {
                result.put(cls, "abstract class");
                continue;
            }

            // Class or interface?
            Matcher mc = CLASS_PATTERN.matcher(content);
            boolean found = false;

            while (mc.find()) {
                String type = mc.group(1);
                String name = mc.group(2);

                if (name.equals(cls)) {
                    result.put(cls, type);
                    found = true;
                    break;
                }
            }

            if (!found) result.put(cls, "class");
        }

        return result;
    }
}