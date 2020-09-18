package io.jenkins.plugins.pipeline;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.sprints.Release;
import io.jenkins.plugins.util.Util;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class AssociateItemToReleaseStep extends AbstractStepImpl {
    private String releasePrefix = null, itemPrefix = null;
    public String getReleasePrefix() {
        return releasePrefix;
    }
    public String getItemPrefix() {
        return itemPrefix;
    }

    @DataBoundConstructor
    public AssociateItemToReleaseStep(String releasePrefix, String itemPrefix) {
        this.releasePrefix = releasePrefix;
        this.itemPrefix = itemPrefix;
    }

    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension(optional = true)
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(AssociateItemToReleaseExecution.class);
        }

        /**
         *
         * @return function name of the add Sprint Comment Action
         */
        @Override
        public String getFunctionName() {
            return "sprintsAssociateItemToRelease";
        }

        /**
         *
         * @return Display name in the UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return "[Zoho Sprints] Associate Item To Release";
        }

    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    public static class AssociateItemToReleaseExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = -7063672993980857830L;

        @Inject
        private transient AssociateItemToReleaseStep step;

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
            String itemPrefix = step.getItemPrefix();

            Release.getInstanceForAssociateItemsForPipeline(run, (BuildListener) listener, releasePrefix, itemPrefix).associateItem();
            return null;
        }
    }

}
