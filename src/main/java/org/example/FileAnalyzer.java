package org.example;

/**
 * Performs simple static analysis on a file's raw content.
 * Counts non-empty lines and estimates control-flow complexity
 * by scanning for branching and loop keywords.
 *
 * @author Xiomara Alcala
 * @author Cory Cowden
 */

public class FileAnalyzer {
    public static FileData analyze(String fileName, String content) {
        if (content == null || content.isEmpty()) {
            return new FileData(fileName, 0, 0);
        }

        String[] lines = content.split("\n");
        int nonEmptyLines = 0;
        int complexity = 0;

        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty()) {
                nonEmptyLines++;

                if (line.startsWith("if") || line.startsWith("for") || line.startsWith("while") || line.startsWith("switch")) {
                    complexity++;
                }

                if (line.contains("if(") || line.contains("for(") || line.contains("while(") || line.contains("switch(")) {
                    complexity++;
                }
            }
        }

        return new FileData(fileName, nonEmptyLines, complexity);
    }
}
