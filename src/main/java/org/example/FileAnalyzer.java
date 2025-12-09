package org.example;

/**
 * Performs basic analysis on a Java source file.
 *
 * This analyzer counts:
 * - Non-empty lines of code
 * - A simple branching complexity score based on occurrences of if/for/while/switch
 *
 * The result is returned as a FileData object, which is later displayed
 * in the grid and file tree panels.
 * @Author Cory Cowden
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
