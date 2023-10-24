package io.jenkins.plugins.actions.postbuild;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.postbuild.builder.ReleasePostBuilder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.api.ReleaseAPI;

public class AddReleaseComment extends ReleasePostBuilder {

    @DataBoundConstructor
    public AddReleaseComment(String projectNumber, String releaseNumber, String note) {
        super(projectNumber, releaseNumber, note);
    }

    @Override
    public String perform() throws Exception {
        return ReleaseAPI.getInstance().addComment(getForm());
    }

    @Extension
    public static class DescriptorImpl extends PostBuildDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.add_release_comment();
        }
    }

}
