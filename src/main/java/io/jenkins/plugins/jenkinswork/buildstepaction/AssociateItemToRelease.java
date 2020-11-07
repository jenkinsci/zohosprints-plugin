package io.jenkins.plugins.jenkinswork.buildstepaction;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.Release;
import io.jenkins.plugins.util.Util;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

public class AssociateItemToRelease extends Builder {
    private String releasePrefix = null, itemPrefix = null;
    public String getReleasePrefix() {
        return releasePrefix;
    }
    public String getItemPrefix() {
        return itemPrefix;
    }

    @DataBoundConstructor
    public AssociateItemToRelease(String releasePrefix, String itemPrefix) {
        this.releasePrefix = releasePrefix;
        this.itemPrefix = itemPrefix;
    }
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return Release.getInstanceForAssociateItems(build, listener, releasePrefix, itemPrefix).associateItem();
    }
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            if(MatrixProject.class.equals(jobType)) {
                return false;
            }
            return true;
        }

        public FormValidation doCheckReleasePrefix(@QueryParameter final String releasePrefix) {
            if (StringUtils.isEmpty(releasePrefix)) {
                return FormValidation.validateRequired(releasePrefix);
            } else if (releasePrefix.matches(Util.RELEASE_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message("Release"));
        }

        public FormValidation doCheckItemPrefix(@QueryParameter final String itemPrefix) {
            if (StringUtils.isEmpty(itemPrefix)) {
                return FormValidation.validateRequired(itemPrefix);
            } else if (itemPrefix.matches(Util.ITEM_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message("Item"));
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
            return Messages.release_associate_item();
        }
    }
}
