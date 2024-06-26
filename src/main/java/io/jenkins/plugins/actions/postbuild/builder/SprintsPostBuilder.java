package io.jenkins.plugins.actions.postbuild.builder;

import io.jenkins.plugins.model.Sprint;

public abstract class SprintsPostBuilder extends PostBuild {

    public SprintsPostBuilder(String projectNumber, String sprintNumber, String name, String description,
            String scrummaster, String users,
            String duration, String startdate, String enddate, String customFields) {
        super(Sprint.getInstance(projectNumber, sprintNumber)
                .setName(name)
                .setDescription(description)
                .setDuration(duration)
                .setStartdate(startdate)
                .setEnddate(enddate)
                .setScrummaster(scrummaster)
                .setUsers(users)
                .setCustomFields(customFields));
    }

    public SprintsPostBuilder(String projectNumber, String sprintNumber, String note) {
        super(Sprint.getInstance(projectNumber, sprintNumber).setNote(note));
    }

    public SprintsPostBuilder(String projectNumber, String sprintNumber) {
        super(Sprint.getInstance(projectNumber, sprintNumber));
    }

    public Sprint getForm() {
        return (Sprint) super.getForm();
    }

    public String getSprintNumber() {
        return getForm().getSprintNumber();
    }

    public String getName() {
        return getForm().getName();
    }

    public String getStartdate() {
        return getForm().getStartdate();
    }

    public String getEnddate() {
        return getForm().getEnddate();
    }

    public String getNote() {
        return getForm().getNote();
    }

    public String getDuration() {
        return getForm().getDuration();
    }

    public String getDescription() {
        return getForm().getDescription();
    }

    public String getUsers() {
        return getForm().getUsers();
    }

    public String getScrummaster() {
        return getForm().getScrummaster();
    }
}
