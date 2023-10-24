package io.jenkins.plugins.actions.buildstepaction;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.ItemStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.WorkItemAPI;

public class AddItemComment extends ItemStepBuilder {

    @DataBoundConstructor
    public AddItemComment(String projectNumber, String sprintNumber, String itemNumber, String note) {
        super(projectNumber, sprintNumber, itemNumber, note);
    }

    @Override
    public String perform() throws Exception {
        return WorkItemAPI.getInstance().addComment(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.add_item_comment();
        }

    }

}
