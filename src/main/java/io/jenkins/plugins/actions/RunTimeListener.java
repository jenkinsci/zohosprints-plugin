package io.jenkins.plugins.actions;

import java.io.IOException;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.api.WorkItemAPI;
import io.jenkins.plugins.model.Item;

@Extension
public class RunTimeListener extends RunListener<Run<?, ?>> {

    @Override
    public void onCompleted(final Run<?, ?> run, final TaskListener listener) {
        EnvVars envVars = null;
        try {
            envVars = run.getEnvironment(listener);
        } catch (IOException | InterruptedException e) {
            listener.error(e.getMessage());
            return;
        }

        Boolean isIssueCreateConfigured = envVars.containsKey("ZSPRINTS_ISSUE_BUILD_ENVIRONMENT_AVAILABLE");
        if (isIssueCreateConfigured && Result.FAILURE.equals(run.getResult())) {
            String projectNumber = envVars.get("ZSPRINTS_ISSUE_PROJECT_NUMBER");
            String sprintNumber = envVars.get("ZSPRINTS_ISSUE_SPRINT_NUMBER");
            String name = envVars.get("ZSPRINTS_ISSUE_NAME");
            String description = envVars.get("ZSPRINTS_ISSUE_DESCRIPTION");
            String assignee = envVars.get("ZSPRINTS_ISSUE_ASSIGNEE");
            String type = envVars.get("ZSPRINTS_ISSUE_TYPE");
            String status = envVars.get("ZSPRINTS_ISSUE_STATUS");
            String duration = envVars.get("ZSPRINTS_ISSUE_DURATION");
            String priority = envVars.get("ZSPRINTS_ISSUE_PRIORITY");
            String startdate = envVars.get("ZSPRINTS_ISSUE_STARTDATE");
            String enddate = envVars.get("ZSPRINTS_ISSUE_ENDDATE");
            String customfield = envVars.get("ZSPRINTS_ISSUE_CUSTOMFIELD");
            Item item = Item.getInstance(projectNumber, sprintNumber).setName(name)
                    .setDescription(description)
                    .setStatus(status)
                    .setType(type)
                    .setPriority(priority)
                    .setDuration(duration)
                    .setAssignee(assignee)
                    .setStartdate(startdate)
                    .setEnddate(enddate)
                    .setCustomFields(customfield);

            item.setEnviroinmentVaribaleReplacer((key) -> {
                try {
                    return run.getEnvironment(listener).expand(key);
                } catch (IOException | InterruptedException e) {
                    return key;
                }
            });
            try {
                String message = WorkItemAPI.getInstance().addItem(item);
                if (message != null) {
                    listener.getLogger().println("[Zoho Sprints] " + message);
                }
            } catch (Exception e) {
                listener.error(e.getMessage());
            }

        }
    }

}
