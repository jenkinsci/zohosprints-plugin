package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.Util;
import io.jenkins.plugins.actions.buildstepaction.builder.BuildStep;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.FeedStatusAPI;
import io.jenkins.plugins.model.FeedStatus;

public class AddFeedStatus extends BuildStep {
    public String getFeed() {
        return getForm().getFeed();
    }

    public FeedStatus getForm() {
        return (FeedStatus) super.getForm();
    }

    @DataBoundConstructor
    public AddFeedStatus(String prefix, String feed) {
        super(FeedStatus.getInstance(prefix, feed));
    }

    @Override
    public String perform() throws Exception {
        return new FeedStatusAPI().addFeed(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        public FormValidation doCheckFeed(@QueryParameter final String feed) {
            return Util.validateRequired(feed);
        }

        @Override
        public String getDisplayName() {
            return Messages.add_feed_status();
        }

    }

}
