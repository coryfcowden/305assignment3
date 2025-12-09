package org.example;

/**
 * Data holder for information extracted from a single Java source file.
 *
 * Stores:
 * - The file's name
 * - The number of non-empty lines of code
 * - A simple complexity score
 *
 * This class is used by the grid visualization and file list panel.
 * @Author Cory Cowden
 */

public class FileData {
    private final String name;
    private final int lines;
    private final int complexity;

    public FileData(String name, int lines, int complexity) {
        this.name = name;
        this.lines = lines;
        this.complexity = complexity;
    }

    public String getName() {
        return name;
    }

    public int getLines() {
        return lines;
    }

    public int getComplexity() {
        return complexity;
    }
}
