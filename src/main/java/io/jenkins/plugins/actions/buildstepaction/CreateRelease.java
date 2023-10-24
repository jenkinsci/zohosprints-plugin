package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.ReleaseStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.ReleaseAPI;

public class CreateRelease extends ReleaseStepBuilder {

    @DataBoundConstructor
    public CreateRelease(String projectNumber, String name, String goal, String stage, String owners,
            String startdate, String enddate, String customFields) {
        super(projectNumber, null, name, owners, goal, stage, startdate, enddate, customFields);
    }

    @Override
    public String perform() throws Exception {
        return ReleaseAPI.getInstance().create(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.release_create();
        }
    }
}
