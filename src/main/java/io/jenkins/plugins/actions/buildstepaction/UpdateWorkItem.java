package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.ItemStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.WorkItemAPI;

public class UpdateWorkItem extends ItemStepBuilder {

    @DataBoundConstructor
    public UpdateWorkItem(String projectNumber, String sprintNumber, String itemNumber, String name, String description,
            String status, String type, String priority,
            String duration, String startdate, String enddate, String customFields) {
        super(projectNumber, sprintNumber, itemNumber, name, description, status, type, priority, duration, null,
                startdate, enddate, customFields);
    }

    @Override
    public String perform() throws Exception {
        return WorkItemAPI.getInstance().updateItem(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.update_item();
        }

    }
}