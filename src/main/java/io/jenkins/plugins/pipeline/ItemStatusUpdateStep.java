
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
import org.json.JSONArray;
import org.kohsuke.stapler.DataBoundConstructor;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.PrintStream;

import static io.jenkins.plugins.util.Util.sprintsLogparser;
import static io.jenkins.plugins.util.Util.parseResponse;

/**
 * ItemStatusUpdateClass used to update sprint item status from pipeline job.
 *
 * @author selvavignesh.m
 * @version 1.0
 */
public class ItemStatusUpdateStep extends AbstractStepImpl {
    private final String prefix;
    private final String status;

    /**
     *
     * @return For Which Sprint Item Status to getupdate
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     *
     * @return Status to be updateds
     */
    public String getStatus() {
        return this.status;
    }

    /**
     *
     * @param prefix For Which Sprint Item Status to getupdate
     * @param status Status to be updated
     */
    @DataBoundConstructor
    public ItemStatusUpdateStep(String prefix, String status) {
        this.prefix = prefix;
        this.status = status;
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension(optional = true)
    public static final class DescriptorImpl
            extends AbstractStepDescriptorImpl {
        public DescriptorImpl() {
            super(StatusUpdateExecution.class);
        }

        /**
         *
         * @return Function name of the update Sprint Item Status Action
         */
        @Override
        public String getFunctionName() {
            return "sprintsUpdateItemStatus";
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

    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    public static class StatusUpdateExecution
            extends AbstractSynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = 6804274094504743740L;
        @Inject
        private transient ItemStatusUpdateStep  step;
        @StepContextParameter
        private transient Run run;
        @StepContextParameter
        private transient TaskListener listener;

        /**
         *
         * @return Void
         * @throws Exception   when exception occurred during rest call
         */
        @Override
        protected Void run() throws Exception {
            PrintStream printStream = listener.getLogger();
            if (!Util.isAuthendicated()) {
                printStream.println("Sprints Authentication Failed");
                return null;
            }
            try {
                SprintsWebHook webHook = SprintsWebHook.getInstatnceForUpdateStatus(
                        run, listener, step.getPrefix(), step.getStatus(), false);
                Object value = parseResponse(webHook.updateItemStatus(),
                        "STATUS_UPDATED_ITEM");
                JSONArray ja = value != null ? new JSONArray(value.toString())
                        : new JSONArray();
                if (ja.length() > 0) {
                    printStream.println(sprintsLogparser(
                                    "Updated Status for following Items " + ja.toString()));
                } else {
                    listener.error(sprintsLogparser(
                            "None of the Items Status are updated"));
                }
            } catch (Exception e) {
                listener.error(sprintsLogparser(
                        "Error occured. None of the Items Status are updated"));
            }

        return null;
        }
    }
}
