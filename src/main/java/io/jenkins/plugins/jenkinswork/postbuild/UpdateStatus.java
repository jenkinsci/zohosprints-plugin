package io.jenkins.plugins.jenkinswork.postbuild;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.*;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.SprintsWorkAction;
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
public class UpdateStatus extends Recorder implements MatrixAggregatable {
    private static  final Logger LOGGER = Logger.getLogger(UpdateStatus.class.getName());
    private String prefix;
    private String status;
    /**
     *
     * @return For Which Sprint Item Status to getupdate
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     *
     * @return Status to be updated
     */
    public String getStatus() {
        return this.status;
    }

    /**
     *
     * @return Need to monitor a build step or not
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    /**
     *
     * @param prefix For Which Sprint Item Status to getupdate
     * @param status Status to be updated
     */
    @DataBoundConstructor
    public UpdateStatus(String prefix, String status) {
        this.prefix = prefix;
        this.status = status;
    }
    private TaskListener taskListener;

    /**
     *
     * @return instance of DescriptorImpl
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    //This method will run this action only in Matrix parent job
    public MatrixAggregator createAggregator(MatrixBuild matrixbuild,
                                             Launcher launcher, BuildListener buildlistener) {
        return new MatrixAggregator(matrixbuild, launcher, buildlistener) {
            @Override
            public boolean endBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINE, "end build of {0}", this.build.getDisplayName());
                return UpdateStatus.this._perform(this.build, this.launcher, this.listener);
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
     * @param build Build Object of Current build
     * @param launcher launcher is responsible for inheriting environment variables
     * @param listener Receives events that happen during a build
     * @return if build success then true, else false
     * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
     * @throws IOException Input/Output error
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        if(build instanceof MatrixRun) {
            return true;
        }
        return _perform(build,launcher,listener);
    }
    public boolean _perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        return SprintsWorkAction.getInstanceForStatusUpdate(build, listener, SprintsWorkAction.POST_BUILD_TYPE, prefix, status).updateStatus();
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         *
         * @param jobType type of job
         * @return All jobs able to use this plugin
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         *
         * @param prefix For Which Sprint Item Status to getupdate
         * @return if prefix matches the regex the OK else Error
         */
        public FormValidation doCheckPrefix(@QueryParameter final String prefix) {

            if (prefix.matches(Util.ITEM_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message("Item"));
        }

        /**
         *
         * @param status Status to be updated
         * @return if param is not null or empty then OK else Error
         */
        public FormValidation doCheckStatus(@QueryParameter final String status) {

            if (!status.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.item_status_message());
        }

        /**
         *
         * @param req staplerrequest Object
         * @param json Contains value and key
         * @return true/false
         * @throws FormException if querying of form throws an error
         */
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return true;
        }

        /**
         *
         * @return Display Name in UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.update_item_status();
        }
    }
}
