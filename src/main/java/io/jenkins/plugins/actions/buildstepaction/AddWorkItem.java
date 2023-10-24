package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.Util;
import io.jenkins.plugins.actions.buildstepaction.builder.ItemStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.WorkItemAPI;

public class AddWorkItem extends ItemStepBuilder {

    @DataBoundConstructor
    public AddWorkItem(String projectNumber, String sprintNumber, String name,
            String description, String status, String type, String priority,
            String duration, String assignee, String startdate, String enddate, String customFields) {
        super(projectNumber, sprintNumber, null, name, description, status, type, priority, duration, assignee,
                startdate, enddate,
                customFields);
    }

    @Override
    public String perform() throws Exception {
        return WorkItemAPI.getInstance().addItem(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {
        @Override
        public String getDisplayName() {
            return Messages.create_item();
        }

        public FormValidation doCheckName(@QueryParameter final String name) {
            return Util.validateRequired(name);
        }

        public FormValidation doCheckStatus(@QueryParameter final String status) {
            return Util.validateRequired(status);
        }

        public FormValidation doCheckType(@QueryParameter final String type) {
            return Util.validateRequired(type);
        }

        public FormValidation doCheckPriority(@QueryParameter final String priority) {
            return Util.validateRequired(priority);
        }

    }
}
