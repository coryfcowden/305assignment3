package org.example;
//test
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple helper that converts a map of className -> sourceContent
 * into a PlantUML string, using heuristic regex rules described by the user.
 *
 * Rules implemented:
 *  - detect classes/interfaces/abstract classes
 *  - detect extends (inheritance)
 *  - detect implements (realization)
 *  - detect fields -> composition/aggregation/association heuristics
 *  - detect method params, return types, local 'new' -> dependency
 *
 * This is intentionally heuristic and not a full parser.
 */
public class UMLGenerator {

    public enum RelType {
        EXTENDS,       // --|>
        REALIZES,      // ..|>
        COMPOSITION,   // *--
        AGGREGATION,   // o--
        ASSOCIATION,   // -->
        DEPENDENCY     // ..>
    }

    // precedence: lower index = stronger relationship
    private static final List<RelType> PRECEDENCE = List.of(
            RelType.EXTENDS,
            RelType.REALIZES,
            RelType.COMPOSITION,
            RelType.AGGREGATION,
            RelType.ASSOCIATION,
            RelType.DEPENDENCY
    );

    public static String generatePlantUML(Map<String, String> classSources) {
        if (classSources == null) classSources = Collections.emptyMap();

        // clean sources (strip comments) and build set of class simple names
        Map<String, String> cleaned = new LinkedHashMap<>();
        Set<String> classNames = new LinkedHashSet<>();
        for (Map.Entry<String, String> e : classSources.entrySet()) {
            String simple = extractSimpleName(e.getKey());
            String content = e.getValue() == null ? "" : stripComments(e.getValue());
            cleaned.put(simple, content);
            classNames.add(simple);
        }

        // detect types (class/interface/abstract)
        Map<String, String> typeDecl = new LinkedHashMap<>(); // name -> "class"|"interface"|"abstract class"
        Pattern classPattern = Pattern.compile("\\b(class|interface)\\s+([A-Z][A-Za-z0-9_]*)");
        Pattern abstractPattern = Pattern.compile("\\babstract\\s+class\\s+([A-Z][A-Za-z0-9_]*)");

        for (String cls : cleaned.keySet()) {
            String content = cleaned.get(cls);
            String lower = content.toLowerCase();

            Matcher mAbs = abstractPattern.matcher(content);
            if (mAbs.find()) {
                String name = mAbs.group(1);
                if (name.equals(cls)) {
                    typeDecl.put(cls, "abstract class");
                    continue;
                }
            }

            Matcher mClass = classPattern.matcher(content);
            boolean found = false;
            while (mClass.find()) {
                String kind = mClass.group(1);
                String name = mClass.group(2);
                if (name.equals(cls)) {
                    if ("interface".equals(kind)) typeDecl.put(cls, "interface");
                    else typeDecl.put(cls, "class");
                    found = true;
                    break;
                }
            }
            if (!found) {
                // fallback default: class
                typeDecl.put(cls, "class");
            }
        }

        // relationships map: key = "A|B", value = RelType (strongest)
        Map<String, RelType> relations = new LinkedHashMap<>();

        // helper to set relation with precedence resolution
        BiConsumerWithException<String, RelType> addRel = (aB, rel) -> {
            RelType existing = relations.get(aB);
            if (existing == null) {
                relations.put(aB, rel);
                return;
            }
            int ex = PRECEDENCE.indexOf(existing);
            int ne = PRECEDENCE.indexOf(rel);
            if (ne < ex) { // new relation stronger -> replace
                relations.put(aB, rel);
            }
        };

        // 1) Extends and Implements
        Pattern extendsPattern = Pattern.compile("\\bclass\\s+([A-Z][A-Za-z0-9_]*)\\s+extends\\s+([A-Za-z0-9_\\.<>]+)");
        Pattern implementsPattern = Pattern.compile("\\bclass\\s+([A-Z][A-Za-z0-9_]*)\\s+implements\\s+([A-Za-z0-9_\\.,\\s<>]+)");

        for (Map.Entry<String, String> e : cleaned.entrySet()) {
            String cls = e.getKey();
            String content = e.getValue();

            Matcher me = extendsPattern.matcher(content);
            while (me.find()) {
                String child = me.group(1);
                String parentFull = me.group(2).trim();
                String parent = simpleFromQualified(parentFull);
                if (classNames.contains(child) && classNames.contains(parent)) {
                    addRel.accept(child + "|" + parent, RelType.EXTENDS);
                }
            }

            Matcher mi = implementsPattern.matcher(content);
            while (mi.find()) {
                String implementer = mi.group(1);
                String list = mi.group(2);
                String[] parts = list.split(",");
                for (String p : parts) {
                    String iface = simpleFromQualified(p.trim());
                    if (classNames.contains(implementer) && classNames.contains(iface)) {
                        addRel.accept(implementer + "|" + iface, RelType.REALIZES);
                    }
                }
            }
        }

        // 2) Fields -> composition/aggregation/association
        // field pattern: (private|protected|public) <type> <name> ;
        Pattern fieldPattern = Pattern.compile("\\b(private|protected|public)\\s+([A-Za-z0-9_\\.<>, ?]+)\\s+([A-Za-z0-9_]+)\\s*;");
        Set<String> collectionKeywords = new HashSet<>(Arrays.asList("List", "Set", "Map", "Collection", "ArrayList", "LinkedList", "HashSet"));

        for (Map.Entry<String, String> e : cleaned.entrySet()) {
            String cls = e.getKey();
            String content = e.getValue();

            Matcher mf = fieldPattern.matcher(content);
            while (mf.find()) {
                String visibility = mf.group(1);
                String typeRaw = mf.group(2).trim();
                String type = extractPrimaryType(typeRaw);

                // ignore primitives
                if (isPrimitive(type)) continue;

                // if fully qualified, take simple
                String simpleType = simpleFromQualified(type);

                if (!classNames.contains(simpleType)) continue;

                // detect collection by raw token containing known collection words or generics with inner type
                boolean isCollection = false;
                for (String kw : collectionKeywords) {
                    if (typeRaw.contains(kw)) {
                        isCollection = true;
                        break;
                    }
                }

                // Heuristics:
                // - private & NOT collection -> composition
                // - public/protected & NOT constructed -> aggregation
                // - collection -> association (we'll treat as association to keep it simple)
                if ("private".equals(visibility) && !isCollection) {
                    addRel.accept(cls + "|" + simpleType, RelType.COMPOSITION);
                } else if (("protected".equals(visibility) || "public".equals(visibility)) && !isCollection) {
                    addRel.accept(cls + "|" + simpleType, RelType.AGGREGATION);
                } else {
                    // collections and other cases -> association
                    addRel.accept(cls + "|" + simpleType, RelType.ASSOCIATION);
                }
            }
        }

        // 3) Method params, return types, local new -> dependency / association
        // method signature pattern: <modifiers> <Type> name( ... )
        Pattern methodSigPattern = Pattern.compile("[\\w<>\\[\\]\\s]*\\b([A-Z][A-Za-z0-9_<>\\., ]+)\\s+[a-zA-Z_][A-Za-z0-9_]*\\s*\\(");
        // param extractor (inside parentheses)
        Pattern paramsPattern = Pattern.compile("\\(([^)]*)\\)");
        // new pattern
        Pattern newPattern = Pattern.compile("new\\s+([A-Z][A-Za-z0-9_]*)\\s*\\(");
        // local var pattern: Type name =
        Pattern localVarPattern = Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\s+[a-zA-Z_][A-Za-z0-9_]*\\s*=");

        for (Map.Entry<String, String> e : cleaned.entrySet()) {
            String cls = e.getKey();
            String content = e.getValue();

            // methods: return types
            Matcher mMethod = methodSigPattern.matcher(content);
            while (mMethod.find()) {
                String retType = mMethod.group(1).trim();
                String primary = extractPrimaryType(retType);
                primary = simpleFromQualified(primary);
                if (classNames.contains(primary) && !primary.equals(cls)) {
                    // return type -> dependency
                    addRel.accept(cls + "|" + primary, RelType.DEPENDENCY);
                }
            }

            // params inside parentheses (iterate method-like occurrences)
            Matcher mParams = paramsPattern.matcher(content);
            while (mParams.find()) {
                String inside = mParams.group(1);
                if (inside.trim().isEmpty()) continue;
                // split by comma and inspect tokens for capitalized types
                String[] parts = inside.split(",");
                for (String part : parts) {
                    // take first token that looks like a type
                    String[] tokens = part.trim().split("\\s+");
                    if (tokens.length == 0) continue;
                    String t = tokens[0].trim();
                    t = extractPrimaryType(t);
                    t = simpleFromQualified(t);
                    if (classNames.contains(t)) {
                        addRel.accept(cls + "|" + t, RelType.DEPENDENCY);
                    }
                }
            }

            // new expressions
            Matcher mNew = newPattern.matcher(content);
            while (mNew.find()) {
                String t = mNew.group(1).trim();
                if (classNames.contains(t) && !t.equals(cls)) {
                    addRel.accept(cls + "|" + t, RelType.DEPENDENCY);
                }
            }

            // local var declarations
            Matcher mLocal = localVarPattern.matcher(content);
            while (mLocal.find()) {
                String t = mLocal.group(1).trim();
                if (classNames.contains(t) && !t.equals(cls)) {
                    addRel.accept(cls + "|" + t, RelType.DEPENDENCY);
                }
            }
        }

        // Build UML string
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        sb.append("!pragma layout smetana\n\n");

        // Add type declarations
        for (String cls : typeDecl.keySet()) {
            String decl = typeDecl.get(cls);
            sb.append(decl).append(" ").append(cls).append("\n");
        }
        sb.append("\n");

        // Add relations
        for (Map.Entry<String, RelType> re : relations.entrySet()) {
            String key = re.getKey();
            String[] parts = key.split("\\|", 2);
            if (parts.length != 2) continue;
            String a = parts[0];
            String b = parts[1];
            RelType rel = re.getValue();

            switch (rel) {
                case EXTENDS:
                    sb.append(a).append(" --|> ").append(b).append("\n");
                    break;
                case REALIZES:
                    sb.append(a).append(" ..|> ").append(b).append("\n");
                    break;
                case COMPOSITION:
                    sb.append(a).append(" *-- ").append(b).append("\n");
                    break;
                case AGGREGATION:
                    sb.append(a).append(" o-- ").append(b).append("\n");
                    break;
                case ASSOCIATION:
                    sb.append(a).append(" --> ").append(b).append("\n");
                    break;
                case DEPENDENCY:
                    sb.append(a).append(" ..> ").append(b).append("\n");
                    break;
            }
        }

        sb.append("\n@enduml\n");
        return sb.toString();
    }

    // util: strip comments (single-line and block)
    private static String stripComments(String src) {
        // remove block comments
        src = src.replaceAll("(?s)/\\*.*?\\*/", " ");
        // remove single-line comments
        src = src.replaceAll("//.*", " ");
        return src;
    }

    // util: primitive detection
    private static boolean isPrimitive(String t) {
        if (t == null) return true;
        String lower = t.toLowerCase();
        return lower.equals("int") || lower.equals("long") || lower.equals("short") ||
                lower.equals("byte") || lower.equals("float") || lower.equals("double") ||
                lower.equals("boolean") || lower.equals("char") || lower.equals("void") ||
                lower.equals("string"); // treat String as primitive for our purposes
    }

    // util: extract main type from generics like List<Bar> -> List ; or Bar -> Bar
    private static String extractPrimaryType(String raw) {
        if (raw == null) return "";
        raw = raw.trim();
        // if generic e.g. List<Bar> -> try to extract inner Bar first
        Matcher mInner = Pattern.compile("<\\s*([A-Za-z0-9_\\.]+)\\s*>").matcher(raw);
        if (mInner.find()) {
            return mInner.group(1);
        }
        // if qualified like com.example.Bar, take last
        if (raw.contains(".")) {
            String[] parts = raw.split("\\.");
            return parts[parts.length - 1];
        }
        // remove array brackets if any
        raw = raw.replaceAll("\\[\\]", "");
        // remove trailing punctuation
        raw = raw.replaceAll("[^A-Za-z0-9_<>]", "");
        return raw;
    }

    // util: simple from qualified (strip package and generics)
    private static String simpleFromQualified(String s) {
        if (s == null) return "";
        s = s.trim();
        // remove generics
        int lt = s.indexOf('<');
        if (lt != -1) s = s.substring(0, lt);
        // remove package
        if (s.contains(".")) {
            String[] parts = s.split("\\.");
            s = parts[parts.length - 1];
        }
        // remove non-identifier chars
        s = s.replaceAll("[^A-Za-z0-9_]", "");
        return s;
    }

    // util: extract simple name from path or dotted name
    private static String extractSimpleName(String name) {
        if (name == null) return "";
        String n = name;
        int slash = Math.max(n.lastIndexOf('/'), n.lastIndexOf('\\'));
        if (slash != -1) n = n.substring(slash + 1);
        if (n.contains(".")) {
            String[] parts = n.split("\\.");
            n = parts[parts.length - 1];
        }
        if (n.endsWith(".java")) n = n.substring(0, n.length() - 5);
        return n;
    }

    // small functional helper to allow throwing from lambda
    @FunctionalInterface
    private interface BiConsumerWithException<T, U> {
        void accept(T t, U u) throws RuntimeException;
    }
}
