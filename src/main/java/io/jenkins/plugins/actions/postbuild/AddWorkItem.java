package io.jenkins.plugins.actions.postbuild;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.postbuild.builder.ItemPostBuilder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.api.WorkItemAPI;

public class AddWorkItem extends ItemPostBuilder {

    @DataBoundConstructor
    public AddWorkItem(String projectNumber, String sprintNumber, String name,
            String description, String status, String type, String priority,
            String duration, String assignee, String startdate, String enddate, String customFields) {
        super(projectNumber, sprintNumber, null, name, description, status, type, priority, duration, assignee,
                startdate, enddate,
                customFields);
    }

    @Override
    public String perform(Function<String, String> replacer) throws Exception {
        return WorkItemAPI.getInstance(replacer).addItem(getForm());
    }

    @Extension
    public static class DescriptorImpl extends PostBuildDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.create_item();
        }

    }

}
