package io.jenkins.plugins.actions.buildstepaction;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
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
    public AddFeedStatus(String projectNumber, String feed) {
        super(FeedStatus.getInstance(projectNumber, feed));
    }

    @Override
    public String perform(Function<String, String> replacer) throws Exception {
        return new FeedStatusAPI().addFeed(getForm(), replacer);
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.add_feed_status();
        }

    }

}
