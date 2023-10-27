package io.jenkins.plugins.model;

public abstract class BaseModel {
    private String projectNumber;

    public BaseModel(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getProjectNumber() {
        return projectNumber;
    }
}
