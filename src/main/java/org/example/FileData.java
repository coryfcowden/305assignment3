package org.example;

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
