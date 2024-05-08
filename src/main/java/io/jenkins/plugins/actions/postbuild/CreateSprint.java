package io.jenkins.plugins.actions.postbuild;

import java.util.function.Function;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.actions.postbuild.builder.SprintsPostBuilder;
import io.jenkins.plugins.actions.postbuild.descriptor.PostBuildDescriptor;
import io.jenkins.plugins.api.SprintAPI;

public class CreateSprint extends SprintsPostBuilder {

    @DataBoundConstructor
    public CreateSprint(String projectNumber, String name, String description, String scrummaster, String users,
            String duration, String startdate, String enddate, String customFields) {
        super(projectNumber, null, name, description, scrummaster, users, duration, startdate, enddate, customFields);
    }

    @Override
    public String perform(Function<String, String> replacer) throws Exception {
        return SprintAPI.getInstance(replacer).create(getForm());
    }

    @Extension
    public static class DescriptorImpl extends PostBuildDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.sprint_create();
        }
    }

}
