package io.jenkins.plugins.actions.buildstepaction;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.buildstepaction.builder.ItemStepBuilder;
import io.jenkins.plugins.actions.buildstepaction.descriptor.BuildStepDescriptorImpl;
import io.jenkins.plugins.api.WorkItemAPI;

public class AddWorkItem extends ItemStepBuilder {

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
    public static class DescriptorImpl extends BuildStepDescriptorImpl {
        @Override
        public String getDisplayName() {
            return Messages.create_item();
        }

    }
}
