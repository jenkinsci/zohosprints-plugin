package io.jenkins.plugins.actions.buildstepaction;

import java.util.function.Function;

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
    public String perform(Function<String, String> replacer) throws Exception {
        return WorkItemAPI.getInstance(replacer).addComment(getForm());
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.add_item_comment();
        }

    }

}
