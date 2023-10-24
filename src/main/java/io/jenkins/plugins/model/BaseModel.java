package io.jenkins.plugins.model;

import java.util.function.Function;

public abstract class BaseModel {
    private String projectNumber;
    protected String releaseNumber, sprintNumber, itemNumber;

    protected Function<String, String> enviroinmentVaribaleReplacer;

    public BaseModel(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public void setEnviroinmentVaribaleReplacer(Function<String, String> enviroinmentVaribaleReplacer) {
        this.enviroinmentVaribaleReplacer = enviroinmentVaribaleReplacer;
    }

    public String getProjectNumber() {
        return getNumbers(projectNumber);
    }

    public String getSprintNumber() {
        return getNumbers(sprintNumber);
    }

    public String getReleaseNumber() {
        return getNumbers(releaseNumber);
    }

    public String getItemNumber() {
        return getNumbers(itemNumber);
    }

    protected String getValue(String key) {
        return enviroinmentVaribaleReplacer == null ? key : enviroinmentVaribaleReplacer.apply(key);
    }

    private String getNumbers(String key) {
        String value = enviroinmentVaribaleReplacer.apply(key);
        Integer.parseInt(value);
        return value;
    }
}
