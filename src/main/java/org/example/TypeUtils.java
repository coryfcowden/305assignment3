package org.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small helper class with a few string utilities used during
 * UML parsing. Nothing fancy here — just basic cleanup and
 * simple name extraction.
 */
public class TypeUtils {
                                                             public static String stripComments(String src) {
        if (src == null) return "";

        // remove /* */ comments
        src = src.replaceAll("(?s)/\\*.*?\\*/", " ");

        // remove // comments
        src = src.replaceAll("//.*", " ");

        return src;
    }

    /**
     * Extracts a simple class name from a path or full file name.
     * Examples:
     *   "src/foo/Bar.java" → "Bar"
     *   "foo.Bar"          → "Bar"
     */
    public static String extractSimpleName(String name) {
        if (name == null) return "";

        // strip directory
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash != -1) {
            name = name.substring(slash + 1);
        }

        // remove .java
        if (name.endsWith(".java")) {
            name = name.substring(0, name.length() - 5);
        }

        // strip any package prefix just in case
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf('.') + 1);
        }

        return name;
    }

    /**
     * Takes something like "com.example.Foo" or "Foo<T>" and extracts just "Foo".
     */
    public static String simpleFromQualified(String s) {
        if (s == null) return "";

        // remove package
        if (s.contains(".")) {
            s = s.substring(s.lastIndexOf('.') + 1);
        }

        // remove leftover punctuation
        return s.replaceAll("[^A-Za-z0-9_]", "");
    }

    /**
     * Extracts the "primary type" from signatures, e.g.:
     *   List<Foo> → Foo
     *   Foo[]     → Foo
     *   com.x.Bar → Bar
     */
    public static String extractPrimaryType(String raw) {
        if (raw == null) return "";

        // check for generics like <Foo>
        Matcher generic = Pattern.compile("<\\s*([A-Za-z0-9_\\.]+)\\s*>").matcher(raw);
        if (generic.find()) {
            return simpleFromQualified(generic.group(1));
        }

        // remove array brackets
        raw = raw.replaceAll("\\[\\]", "");

        // keep only basic identifier characters
        raw = raw.replaceAll("[^A-Za-z0-9_\\.]", "");

        return simpleFromQualified(raw);
    }
}
