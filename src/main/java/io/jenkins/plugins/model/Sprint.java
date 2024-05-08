package io.jenkins.plugins.model;

public class Sprint extends BaseModel {
    private String name, description, startdate, enddate, duration, users, note, scrummaster, customFields,
            sprintNumber;

    public String getScrummaster() {
        return scrummaster;
    }

    public Sprint setScrummaster(String scrummaster) {
        this.scrummaster = scrummaster;
        return this;
    }

    public String getDurationType() {
        return description;
    }

    public Sprint setName(String name) {
        this.name = name;
        return this;
    }

    public String getSprintNumber() {
        return sprintNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Sprint setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getStartdate() {
        return startdate;
    }

    public Sprint setStartdate(String startdate) {
        this.startdate = startdate;
        return this;
    }

    public String getEnddate() {
        return enddate;
    }

    public String getCustomFields() {
        return customFields;
    }

    public Sprint setEnddate(String enddate) {
        this.enddate = enddate;
        return this;
    }

    public String getDuration() {
        return duration;
    }

    public Sprint setDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public String getUsers() {
        return users;
    }

    public Sprint setUsers(String users) {
        this.users = users;
        return this;
    }

    public String getNote() {
        return note;
    }

    public Sprint setNote(String note) {
        this.note = note;
        return this;
    }

    private Sprint setSprintNumber(String sprintNumber) {
        this.sprintNumber = sprintNumber;
        return this;
    }

    public Sprint setCustomFields(String customFields) {
        this.customFields = customFields;
        return this;
    }

    private Sprint(String projectNumber) {
        super(projectNumber);
    }

    public static Sprint getInstance(String projectNumber, String sprintNumber) {
        return new Sprint(projectNumber).setSprintNumber(sprintNumber);
    }

}
