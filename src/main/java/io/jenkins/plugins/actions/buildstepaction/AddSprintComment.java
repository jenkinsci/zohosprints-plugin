package io.jenkins.plugins.actions.buildstepaction;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.SprintsStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.SprintAPI;

public class AddSprintComment extends SprintsStepBuilder {
    @DataBoundConstructor
    public AddSprintComment(String projectNumber, String sprintNumber, String note) {
        super(projectNumber, sprintNumber, note);
    }

    @Override
    public String perform(Function<String, String> replacer) throws Exception {
        return SprintAPI.getInstance(replacer).addComment(getForm());

    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.add_sprint_comment();
        }

    }

}
