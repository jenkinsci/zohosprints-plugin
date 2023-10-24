package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.ReleaseStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.ReleaseAPI;

public class AddReleaseComment extends ReleaseStepBuilder {

    @DataBoundConstructor
    public AddReleaseComment(String projectNumber, String releaseNumber, String note) {
        super(projectNumber, releaseNumber, note);
    }

    @Override
    public String perform() throws Exception {
        return ReleaseAPI.getInstance().addComment(getForm());

    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.add_release_comment();
        }

    }
}
