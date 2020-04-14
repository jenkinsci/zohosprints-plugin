package io.jenkins.plugins.pipeline;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.SprintsWebHook;
import io.jenkins.plugins.util.Util;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import static io.jenkins.plugins.util.Util.sprintsLogparser;
import static io.jenkins.plugins.util.Util.parseResponse;
/**
 * ItemPriorityUpdateStep used to update sprints item Priority from pipeline job.
 *
 * @author selvavignesh.m
 * @version 1.0
 */
public class ItemPriorityUpdateStep extends AbstractStepImpl {
    private final String prefix;
    private final String priority;

    /**
     *
     * @param prefix For Which Sprint Item priority to getupdate
     * @param priority priority to be updated
     */
    @DataBoundConstructor
    public ItemPriorityUpdateStep(String prefix, String priority) {
        this.prefix = prefix;
        this.priority = priority;
    }

    /**
     *
     * @return priority to be updated
     */
    public String getPriority() {
        return priority;
    }
    /**
     *
     * @return For Which Sprint Item priority to getupdate
     */
    public String getPrefix() {
        return prefix;
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension(optional = true)
    public static final class DescriptorImpl
            extends AbstractStepDescriptorImpl {
        public DescriptorImpl() {
            super(PrioriryUpdateExecution.class);
        }

        /**
         *
         * @return function name of the update Sprint Item priority Action
         */
        @Override
        public String getFunctionName() {
            return "sprintsUpdateItemPriority";
        }

        /**
         *
         * @return Display name in UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.update_priority();
        }
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    public static class PrioriryUpdateExecution
            extends AbstractSynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = -6992411735830273575L;
        @Inject
        private transient ItemPriorityUpdateStep step;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient Run run;

        /**
         *
         * @return Void
         * @throws Exception  when exception occurred during rest call
         */
        @Override
        protected Void run() throws Exception {
            if (!Util.isAuthendicated()) {
                listener.getLogger().println("Sprints Authentication Failed");
                return null;
            }
            try {
                SprintsWebHook sp = SprintsWebHook.getInstatnceForUpdatePriority(run, listener, step.getPrefix(), step.getPriority());
                Object respObj = parseResponse(sp.updateItempriority(), "UPDATED_ITEM_PRIORITY");
                String respString = respObj != null ? (String) respObj : null;
                if (respString != null && !respString.isEmpty()) {
                    listener.getLogger().println(Util.sprintsLogparser("Updated Priority for following Items " + respString));
                } else {
                    listener.error(sprintsLogparser("None of the Item priority updated"));
                }
            } catch (Exception e) {
                listener.error(sprintsLogparser("Error occured. None of the Item priority updated"));
            }


            return null;
        }
    }
}
