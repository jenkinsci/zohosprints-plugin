package io.jenkins.plugins.jenkinswork.postbuild;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class AssociateItemToRelease extends Recorder implements MatrixAggregatable {
    private static final Logger LOGGER = Logger.getLogger(AssociateItemToRelease.class.getName());
    private String releasePrefix = null, itemPrefix = null;
    public String getReleasePrefix() {
        return releasePrefix;
    }
    public String getItemPrefix() {
        return itemPrefix;
    }

    /**
     *
     * @param releasePrefix
     * @param itemPrefix
     */
    @DataBoundConstructor
    public AssociateItemToRelease(String releasePrefix, String itemPrefix) {
        this.releasePrefix = releasePrefix;
        this.itemPrefix = itemPrefix;
    }
    //This method will run this action only in Matrix parent job
    public MatrixAggregator createAggregator(MatrixBuild matrixbuild,
                                             Launcher launcher, BuildListener buildlistener) {
        return new MatrixAggregator(matrixbuild, launcher, buildlistener) {
            @Override
            public boolean endBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINE, "end build of {0}", this.build.getDisplayName());
                return AssociateItemToRelease.this._perform(this.build, this.launcher, this.listener);
            }

            @Override
            public boolean startBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINE, "end build of {0}", this.build.getDisplayName());
                return true;
            }
        };
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
        if(build instanceof MatrixRun){
            return true;
        }
        return _perform(build,launcher,listener);
    }
    private boolean _perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return Release.getInstanceForAssociateItems(build, listener, releasePrefix, itemPrefix).associateItem();
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
            return FormValidation.error(Messages.prefix_message("Release"));
        }

        public FormValidation doCheckItemPrefix(@QueryParameter final String itemPrefix) {
            if (itemPrefix.matches(Util.ITEM_REGEX)) {
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
