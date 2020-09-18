package io.jenkins.plugins.pipeline;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.jenkinswork.buildstepaction.CreateRelease;
import io.jenkins.plugins.sprints.Release;
import io.jenkins.plugins.util.Util;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class CreateReleaseStep extends AbstractStepImpl {
    private String prefix = null, itemPrefix = null, releaseName = null, description = null, stage = null, owner = null, period = null;
    public String getPrefix() {
        return prefix;
    }

    public String getItemPrefix() {
        return itemPrefix;
    }

    public String getReleaseName() {
        return releaseName;
    }

    public String getDescription() {
        return description;
    }

    public String getStage() {
        return stage;
    }

    public String getOwner() {
        return owner;
    }

    public String getPeriod() {
        return period;
    }

    @DataBoundConstructor
    public CreateReleaseStep(String prefix, String itemPrefix, String releaseName, String description, String stage, String owner, String period) {
        this.prefix = prefix;
        this.itemPrefix = itemPrefix;
        this.releaseName = releaseName;
        this.description = description;
        this.stage = stage;
        this.owner = owner;
        this.period = period;
    }

    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension(optional = true)
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(CreateReleaseExecution.class);
        }

        /**
         *
         * @return function name of the add Sprint Comment Action
         */
        @Override
        public String getFunctionName() {
            return "sprintsCreateRelease";
        }

        /**
         *
         * @return Display name in the UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return "[ZohoSprints] Create Release";
        }

    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    public static class CreateReleaseExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = -7063672993980857830L;

        @Inject
        private transient CreateReleaseStep step;

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
            String prefix = step.getPrefix();
            String itemPrefix = step.getItemPrefix();
            String releaseName = step.getReleaseName();
            String stage = step.getStage();
            String description = step.getDescription();
            String owner = step.getOwner();
            String period = step.getPeriod();

            Release.getInstanceForCreateForPipeline(run, (BuildListener) listener, prefix, itemPrefix, releaseName, stage, description, owner, period).create();
            return null;
        }
    }

}
