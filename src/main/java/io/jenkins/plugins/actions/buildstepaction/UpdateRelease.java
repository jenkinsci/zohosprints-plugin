package io.jenkins.plugins.actions.buildstepaction;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.ReleaseStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.ReleaseAPI;

public class UpdateRelease extends ReleaseStepBuilder {

    @DataBoundConstructor
    public UpdateRelease(String projectNumber, String releaseNumber, String name, String goal, String stage,
            String startdate,
            String enddate, String customFields) {
        super(projectNumber, releaseNumber, name, null, goal, stage, startdate, enddate, customFields);
    }

    @Override
    public String perform(Function<String, String> replacer) throws Exception {
        return ReleaseAPI.getInstance(replacer).update(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.release_update();
        }
    }
}
