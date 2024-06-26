package io.jenkins.plugins.actions.postbuild;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.postbuild.builder.ItemPostBuilder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.api.WorkItemAPI;

public class AddItemComment extends ItemPostBuilder {

    @DataBoundConstructor
    public AddItemComment(String projectNumber, String sprintNumber, String itemNumber, String note) {
        super(projectNumber, sprintNumber, itemNumber, note);
    }

    @Override
    public String perform(Function<String, String> replacer) throws Exception {
        return WorkItemAPI.getInstance(replacer).addComment(getForm());
    }

    @Extension
    public static class DescriptorImpl extends PostBuildDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.add_item_comment();
        }
    }

}
