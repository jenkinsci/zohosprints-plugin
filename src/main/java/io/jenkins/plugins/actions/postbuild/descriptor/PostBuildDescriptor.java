package io.jenkins.plugins.actions.postbuild.descriptor;

import org.kohsuke.stapler.QueryParameter;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import io.jenkins.plugins.Util;

public class PostBuildDescriptor extends BuildStepDescriptor<Publisher> {

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return true;
    }

    public FormValidation doCheckProjectNumber(@QueryParameter final String projectNumber) {
        return Util.validateRequired(projectNumber);
    }

    public FormValidation doCheckSprintNumber(@QueryParameter final String sprintNumber) {
        return Util.validateRequired(sprintNumber);
    }

    public FormValidation doCheckItemNumber(@QueryParameter final String itemNumber) {
        return Util.validateRequired(itemNumber);
    }

    public FormValidation doCheckReleaseNumber(@QueryParameter final String releaseNumber) {
        return Util.validateRequired(releaseNumber);
    }

    public FormValidation doCheckNote(@QueryParameter final String note) {
        return Util.validateRequired(note);
    }
}
