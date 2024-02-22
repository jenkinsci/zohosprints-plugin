package io.jenkins.plugins.actions;

import static io.jenkins.plugins.Util.validateRequired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.Util;
import io.jenkins.plugins.model.Item;
import jenkins.tasks.SimpleBuildWrapper;

public class AddWorkItemOnFailure extends SimpleBuildWrapper {
    private Item item;

    @DataBoundConstructor
    public AddWorkItemOnFailure(String projectNumber, String sprintNumber, String name, String description,
            String status, String type,
            String priority,
            String duration, String assignee, String startdate, String enddate, String customFields) {
        item = Item.getInstance(projectNumber, sprintNumber)
                .setName(name)
                .setDescription(description)
                .setStatus(status)
                .setType(type)
                .setPriority(priority)
                .setDuration(duration)
                .setAssignee(assignee)
                .setStartdate(startdate)
                .setEnddate(enddate)
                .setCustomFields(customFields);
    }

    public Item getForm() {
        return this.item;
    }

    public String getProjectNumber() {
        return item.getProjectNumber();
    }

    public String getSprintNumber() {
        return item.getSprintNumber();
    }

    public String getAssignee() {
        return item.getAssignee();
    }

    public String getName() {
        return item.getName();
    }

    public String getDescription() {
        return item.getDescription();
    }

    public String getStatus() {
        return item.getStatus();
    }

    public String getType() {
        return item.getType();
    }

    public String getPriority() {
        return item.getPriority();
    }

    public String getDuration() {
        return item.getDuration();
    }

    public String getStartdate() {
        return item.getStartdate();
    }

    public String getEnddate() {
        return item.getEnddate();
    }

    public String getCustomFields() {
        return item.getCustomFields();
    }

    public String getNote() {
        return item.getNote();
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher,
            TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {
        final Map<String, String> issueParamMap = new HashMap<>();
        issueParamMap.put("ZSPRINTS_ISSUE_NAME", getName());
        issueParamMap.put("ZSPRINTS_ISSUE_PROJECT_NUMBER", getProjectNumber());
        issueParamMap.put("ZSPRINTS_ISSUE_SPRINT_NUMBER", getSprintNumber());
        issueParamMap.put("ZSPRINTS_ISSUE_DESCRIPTION", getDescription());
        issueParamMap.put("ZSPRINTS_ISSUE_ASSIGNEE", getAssignee());
        issueParamMap.put("ZSPRINTS_ISSUE_TYPE", getType());
        issueParamMap.put("ZSPRINTS_ISSUE_STATUS", getStatus());
        issueParamMap.put("ZSPRINTS_ISSUE_DURATION", getDuration());
        issueParamMap.put("ZSPRINTS_ISSUE_PRIORITY", getPriority());
        issueParamMap.put("ZSPRINTS_ISSUE_STARTDATE", getStartdate());
        issueParamMap.put("ZSPRINTS_ISSUE_ENDDATE", getEnddate());
        issueParamMap.put("ZSPRINTS_ISSUE_CUSTOMFIELD", getCustomFields());
        issueParamMap.put("ZSPRINTS_ISSUE_BUILD_ENVIRONMENT_AVAILABLE", Boolean.toString(true));
        context.getEnv().putAll(issueParamMap);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;

        }

        @Override
        public String getDisplayName() {
            return Messages.issue_in_failure();
        }

        public FormValidation doCheckProjectNumber(@QueryParameter final String projectNumber) {
            return Util.validateRequired(projectNumber);
        }

        public FormValidation doCheckSprintNumber(@QueryParameter final String sprintNumber) {
            return Util.validateRequired(sprintNumber);
        }

        public FormValidation doCheckName(@QueryParameter final String name) {
            return validateRequired(name);
        }

        public FormValidation doCheckStatus(@QueryParameter final String status) {
            return validateRequired(status);
        }

        public FormValidation doCheckType(@QueryParameter final String type) {
            return validateRequired(type);
        }

        public FormValidation doCheckPriority(@QueryParameter final String priority) {
            return validateRequired(priority);
        }
    }
}
