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
import io.jenkins.plugins.sprints.SprintsWorkAction;
import io.jenkins.plugins.util.Util;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class FeedStatus extends Builder {
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
     * @param prefix In which Project feed to be pushed
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
     * @param status Feed Status to be pushed
     */
    public void setStatus(String status) {
        this.status = status;
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
     * @param build Build Object of Current build
     * @param launcher launcher is responsible for inheriting environment variables
     * @param listener Receives events that happen during a build
     * @return if build success then true, else false
     * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
     * @throws IOException Input/Output error
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println(prefix);
        return SprintsWorkAction.getInstanceForFeedStatus(build, listener, SprintsWorkAction.BUILD_TYPE, prefix, status).addFeedStatus();
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
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         *
         * @param jobType Project object
         * @return All jobs able to use this plugin
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {

            if(MatrixProject.class.equals(jobType)) {
                return false;
            }
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
        public FormValidation doCheckPrefix(@QueryParameter String prefix) {
            if(StringUtils.isEmpty(prefix)) {
                return FormValidation.validateRequired(prefix);
            } else if (prefix.matches(Util.PROJECT_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message("Project"));
        }

        /**
         *
         * @param value Feed Status to be pushed
         * @return if param is not null or empty then OK else Error
         */
        public FormValidation doCheckStatus(@QueryParameter String value) {
            if(StringUtils.isEmpty(value)) {
                return FormValidation.validateRequired(value);
            } else if (!value.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.feed_status_message());
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
            return super.configure(req, json);
        }
    }
}
