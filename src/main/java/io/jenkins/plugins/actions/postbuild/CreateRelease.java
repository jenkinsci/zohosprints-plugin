package io.jenkins.plugins.actions.postbuild;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.postbuild.builder.ReleasePostBuilder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.api.ReleaseAPI;

public class CreateRelease extends ReleasePostBuilder {

    @DataBoundConstructor
    public CreateRelease(String projectNumber, String name, String goal, String stage, String owners,
            String startdate, String enddate, String customFields) {
        super(projectNumber, null, name, owners, goal, stage, startdate, enddate, customFields);
    }

    @Override
    public String perform(Function<String, String> replacer) throws Exception {
        return ReleaseAPI.getInstance(replacer).create(getForm());
    }

    @Extension
    public static class DescriptorImpl extends PostBuildDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.release_create();
        }
    }
}
