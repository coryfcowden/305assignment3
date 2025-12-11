package org.example;


import java.util.regex.*;

public class TypeUtils {

    public static String stripComments(String src) {
        src = src.replaceAll("(?s)/\\*.*?\\*/", " ");
        src = src.replaceAll("//.*", " ");
        return src;
    }

    public static String extractSimpleName(String name) {
        if (name == null) return "";
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash != -1) name = name.substring(slash + 1);
        if (name.endsWith(".java")) name = name.substring(0, name.length() - 5);
        if (name.contains(".")) name = name.substring(name.lastIndexOf('.') + 1);
        return name;
    }

    public static String simpleFromQualified(String s) {
        if (s == null) return "";
        if (s.contains(".")) s = s.substring(s.lastIndexOf('.') + 1);
        return s.replaceAll("[^A-Za-z0-9_]", "");
    }

    public static String extractPrimaryType(String raw) {
        if (raw == null) return "";

        Matcher mg = Pattern.compile("<\\s*([A-Za-z0-9_\\.]+)\\s*>").matcher(raw);
        if (mg.find()) return simpleFromQualified(mg.group(1));

        raw = raw.replaceAll("\\[\\]", "");
        raw = raw.replaceAll("[^A-Za-z0-9_\\.]", "");

        return simpleFromQualified(raw);
    }
}