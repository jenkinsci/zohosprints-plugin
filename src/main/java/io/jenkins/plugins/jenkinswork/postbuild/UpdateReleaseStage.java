package io.jenkins.plugins.jenkinswork.postbuild;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.*;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.Release;
import io.jenkins.plugins.util.Util;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class UpdateReleaseStage extends Recorder {
    private String releasePrefix = null, stage = null;

    public String getReleasePrefix() {
        return releasePrefix;
    }

    public String getStage() {
        return stage;
    }

    @DataBoundConstructor
    public UpdateReleaseStage(String releasePrefix, String stage) {
        this.releasePrefix = releasePrefix;
        this.stage = stage;
    }
    /**
     *
     * @return Monitoring Service for BuildStep
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return Release.getInstanceForUpdateStage(build, listener, releasePrefix, stage).updateReleaseStage();
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
        public FormValidation doCheckReleasePrefix(@QueryParameter final String releasePrefix) {
            if (releasePrefix.matches(Util.RELEASE_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        public FormValidation doCheckStage(@QueryParameter final String stage) {
            if (!isEmpty(stage)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }


        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.release_update_stage();
        }
    }

}
