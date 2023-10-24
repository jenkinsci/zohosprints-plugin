package io.jenkins.plugins.model;

public class Release extends BaseModel {
    private String name, goal, stage, startdate, enddate, customFields, owners, note;

    public String getNote() {
        return note;
    }

    public String getName() {
        return getValue(name);
    }

    public String getGoal() {
        return getValue(goal);
    }

    public String getStage() {
        return getValue(stage);
    }

    public String getStartdate() {
        return getValue(startdate);
    }

    public String getEnddate() {
        return getValue(enddate);
    }

    public String getCustomFields() {
        return getValue(customFields);
    }

    public String getOwners() {
        return getValue(owners);
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
