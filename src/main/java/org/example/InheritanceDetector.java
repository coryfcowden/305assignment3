package org.example;

import java.util.*;
import java.util.regex.*;

public class InheritanceDetector {

    private static final Pattern EXTENDS =
            Pattern.compile("\\bclass\\s+([A-Z][A-Za-z0-9_]*)\\s+extends\\s+([A-Za-z0-9_\\.<>]+)");

    private static final Pattern IMPLEMENTS =
            Pattern.compile("\\bclass\\s+([A-Z][A-Za-z0-9_]*)\\s+implements\\s+([A-Za-z0-9_\\.,\\s<>]+)");

    public void detect(Map<String, String> cleaned,
                       Set<String> classNames,
                       Map<String, Relation.RelType> out) {

        for (var entry : cleaned.entrySet()) {
            String content = entry.getValue();

            // EXTENDS
            Matcher mExt = EXTENDS.matcher(content);
            while (mExt.find()) {
                String child = mExt.group(1);
                String parent = TypeUtils.simpleFromQualified(mExt.group(2));
                if (classNames.contains(child) && classNames.contains(parent)) {
                    Relation.addRelation(out, child, parent, Relation.RelType.EXTENDS);
                }
            }

            // IMPLEMENTS
            Matcher mImp = IMPLEMENTS.matcher(content);
            while (mImp.find()) {
                String cls = mImp.group(1);
                for (String iface : mImp.group(2).split(",")) {
                    String simple = TypeUtils.simpleFromQualified(iface.trim());
                    if (classNames.contains(cls) && classNames.contains(simple)) {
                        Relation.addRelation(out, cls, simple, Relation.RelType.REALIZES);
                    }
                }
            }
        }
    }
}