package com.example.model;

public class DsaProblem {
    private String id;
    private String title;
    private String description;
    private String exampleInput;
    private String exampleOutput;

    public DsaProblem(String id, String title, String description, String exampleInput, String exampleOutput) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.exampleInput = exampleInput;
        this.exampleOutput = exampleOutput;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getExampleInput() {
        return exampleInput;
    }

    public String getExampleOutput() {
        return exampleOutput;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExampleInput(String exampleInput) {
        this.exampleInput = exampleInput;
    }

    public void setExampleOutput(String exampleOutput) {
        this.exampleOutput = exampleOutput;
    }

    @Override
    public String toString() {
        return "DsaProblem{" +
               "id='" + id + '\'' +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
