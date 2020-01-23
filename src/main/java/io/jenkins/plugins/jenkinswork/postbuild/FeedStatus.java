package io.jenkins.plugins.jenkinswork.postbuild;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
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
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class FeedStatus extends Recorder {
    private String prefix;
    private String status;

    /**
     *
     * @return In which Project feed to be pushed
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @param fromprefix In which Project feed to be pushed
     */
    public void setPrefix(final String fromprefix) {
        this.prefix = fromprefix;
    }

    /**
     *
     * @return Feed Status to be pushed
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param fromstatus Feed Status to be pushed
     */
    public void setStatus(final String fromstatus) {
        this.status = fromstatus;
    }


    /**
     *
     * @param prefix In which Project feed to be pushed
     * @param status Feed Status to be pushed
     */
    @DataBoundConstructor
    public FeedStatus(String prefix, String status) {
        this.prefix = prefix;
        this.status = status;
    }
    /**
     *
     * @return Monitoring for Build Step
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    /**
     *
     * @param build Build Object of Current build
     * @param launcher launcher is responsible for inheriting environment variables
     * @param listener Receives events that happen during a build
     * @return if build success then true, else false
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return SprintsWorkAction.getInstanceForFeedStatus(build, listener, SprintsWorkAction.POST_BUILD_TYPE, prefix, status).addFeedStatus();
    }

    /**
     *
     * @return instance of DescriptorImpl
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         *
         * @param jobType Project object
         * @return All jobs able to use this plugin
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {

            return true;
        }

        /**
         *
         * @return Display Name in UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.add_feed_status();
        }

        /**
         *
         * @param prefix In which Project feed to be pushed
         * @return if prefix matches the regex the OK else Error
         */
        public FormValidation doCheckPrefix(@QueryParameter final String prefix) {
            if (prefix.matches(Util.PROJECT_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        /**
         *
         * @param value Feed Status to be pushed
         * @return if param is not null or empty then OK else Error
         */
        public FormValidation doCheckStatus(@QueryParameter final String value) {
            if (!value.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.feed_status_message());
        }

        /**
         *
         * @param req staplerrequest Object
         * @param json Contains value and key
         * @return true/false
         * @throws FormException
         */
        @Override
        public boolean configure (StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return super.configure(req, json);
        }
    }
}