package io.jenkins.plugins.model;

public class Release extends BaseModel {
    private String name, goal, stage, startdate, enddate, customFields, owners, note, releaseNumber;

    public String getNote() {
        return note;
    }

    public String getReleaseNumber() {
        return releaseNumber;
    }

    public String getName() {
        return name;
    }

    public String getGoal() {
        return goal;
    }

    public String getStage() {
        return stage;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public String getCustomFields() {
        return customFields;
    }

    public String getOwners() {
        return owners;
    }

    public Release setNote(String note) {
        this.note = note;
        return this;
    }

    public Release setName(String name) {
        this.name = name;
        return this;
    }

    public Release setGoal(String goal) {
        this.goal = goal;
        return this;
    }

    public Release setStage(String stage) {
        this.stage = stage;
        return this;
    }

    public Release setStartdate(String startdate) {
        this.startdate = startdate;
        return this;
    }

    public Release setEnddate(String enddate) {
        this.enddate = enddate;
        return this;
    }

    public Release setCustomFields(String customFields) {
        this.customFields = customFields;
        return this;
    }

    public Release setOwners(String owners) {
        this.owners = owners;
        return this;
    }

    public Release setRelaseNumber(String relaseNumber) {
        this.releaseNumber = relaseNumber;
        return this;
    }

    private Release(String projectNumber) {
        super(projectNumber);
    }

    public static Release getInstance(String projectNumber) {
        return new Release(projectNumber);
    }

}
