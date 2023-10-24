package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.Util;
import io.jenkins.plugins.actions.buildstepaction.builder.SprintsStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.SprintAPI;

public class CreateSprint extends SprintsStepBuilder {

    @DataBoundConstructor
    public CreateSprint(String projectNumber, String name, String description, String scrummaster, String users,
            String duration, String startdate, String enddate, String customFields) {
        super(projectNumber, null, name, description, scrummaster, users, duration, startdate, enddate, customFields);
    }

    @Override
    public String perform() throws Exception {
        return SprintAPI.getInstance().create(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {
        @Override
        public String getDisplayName() {
            return Messages.sprint_create();
        }

        public FormValidation doCheckName(@QueryParameter final String name) {
            return Util.validateRequired(name);
        }
    }

}
