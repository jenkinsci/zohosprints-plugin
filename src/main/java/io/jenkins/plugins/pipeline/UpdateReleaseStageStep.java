package io.jenkins.plugins.pipeline;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.Release;
import io.jenkins.plugins.sprints.SprintsWebHook;
import io.jenkins.plugins.util.Util;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static io.jenkins.plugins.util.Util.sprintsLogparser;

public class UpdateReleaseStageStep extends AbstractStepImpl {
    private String releasePrefix = null, stage = null;

    public String getReleasePrefix() {
        return releasePrefix;
    }

    public String getStage() {
        return stage;
    }
    @DataBoundConstructor
    public UpdateReleaseStageStep(String releasePrefix, String stage) {
        this.releasePrefix = releasePrefix;
        this.stage = stage;
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension(optional = true)
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(UpdateReleaseStageExecution.class);
        }

        /**
         *
         * @return function name of the add Sprint Comment Action
         */
        @Override
        public String getFunctionName() {
            return "sprintsUpdateReleaseStage";
        }

        /**
         *
         * @return Display name in the UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return "[ZohoSprints] Update Release Stage";
        }

    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    public static class UpdateReleaseStageExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = -7063672993980857830L;

        @Inject
        private transient UpdateReleaseStageStep step;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient Run run;

        /**
         *
         * @return Void
         * @throws Exception when exception occurred during rest call
         */
        @Override
        protected Void run() throws Exception {
            if (!Util.isAuthendicated()) {
                listener.getLogger().println("Sprints Authentication Failed");
                return null;
            }
            Object obj = null;
            String releasePrefix = step.getReleasePrefix();
            String releaseStage = step.getStage();
            Release.getInstanceForUpdateStageForPipeline(run, (BuildListener) listener, releasePrefix, releaseStage).updateReleaseStage();
            return null;
        }
    }
}
