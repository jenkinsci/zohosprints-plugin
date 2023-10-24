package io.jenkins.plugins.actions.postbuild;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.postbuild.builder.ItemPostBuilder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.api.WorkItemAPI;

public class UpdateWorkItem extends ItemPostBuilder {

    @DataBoundConstructor
    public UpdateWorkItem(String projectNumber, String sprintNumber, String itemNumber, String name, String description,
            String status, String type, String priority,
            String duration, String startdate, String enddate, String customFields) {
        super(projectNumber, sprintNumber, itemNumber, name, description, status, type, priority, duration, null,
                startdate, enddate, customFields);
    }

    @Override
    public String perform() throws Exception {
        return WorkItemAPI.getInstance().updateItem(getForm());
    }

    @Extension
    public static class DescriptorImpl extends PostBuildDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.update_item();
        }

    }
}
