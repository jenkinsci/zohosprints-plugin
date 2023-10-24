package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.BuildStep;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.SprintAPI;
import io.jenkins.plugins.model.Sprint;

public class CompleteSprint extends BuildStep {

    @DataBoundConstructor
    public CompleteSprint(String projectNumber, String sprintNumber) {
        super(Sprint.getInstance(projectNumber, sprintNumber));
    }

    public Sprint getForm() {
        return (Sprint) super.getForm();
    }

    @Override
    public String perform() throws Exception {
        return SprintAPI.getInstance().complete(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.update_sprint_complete();
        }

    }
}
