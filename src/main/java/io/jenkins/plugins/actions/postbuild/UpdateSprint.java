package io.jenkins.plugins.actions.postbuild;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.postbuild.builder.SprintsPostBuilder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.api.SprintAPI;

public class UpdateSprint extends SprintsPostBuilder {
    @DataBoundConstructor
    public UpdateSprint(String projectNumber, String sprintNumber, String name, String description, String duration,
            String startdate, String enddate, String customFields) {
        super(projectNumber, sprintNumber, name, description, null, null, duration, startdate, enddate, customFields);
    }

    @Override
    public String perform() throws Exception {
        return SprintAPI.getInstance().update(getForm());
    }

    @Extension
    public static class DescriptorImpl extends PostBuildDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.sprint_update();
        }
    }

}
