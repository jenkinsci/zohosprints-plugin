package io.jenkins.plugins.pipeline;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.Messages;
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

/**
 * ItemCommentStep used to Add Comment in Sprints item from pipeline job.
 *
 * @author selvavignesh.m
 * @version 1.0
 */
public class ItemCommentStep extends AbstractStepImpl {

    private final String prefix;
    private final String note;
    private final String logfile;

    /**
     *
     * @param prefix In which Sprint Item comment to be added
     * @param note Comment to be added in Sprint Item
     * @param logfile Comment Attachment String
     */
    @DataBoundConstructor
    public ItemCommentStep(String prefix, String note, String logfile) {
        this.prefix = prefix;
        this.note = note;
        this.logfile = logfile;
    }

    /**
     *
     * @return In which Sprint Item comment to be added
     */
    public String getPrefix() {
        return prefix;
    }
    /**
     *
     * @return Commnt to be added in Sprint Item
     */
    public String getNote() {
        return note;
    }
    /**
     *
     * @return Comment Attachment String
     */
    public String getLogfile() {
        return logfile;
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension(optional = true)
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(CommentStepExecution.class);
        }

        /**
         *
         * @return function name of the add Sprint Comment Action
         */
        @Override
        public String getFunctionName() {
            return "sprintsAddItemComment";
        }

        /**
         *
         * @return Display name in the UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.add_comment();
        }

        /**
         *
         * @return ListBoxModel data of Sprint Item Attachment option
         */
        public ListBoxModel doFillLogfileItems() {
            ListBoxModel m = new ListBoxModel();
            m.add("Yes add", "true");
            m.add("No, don't add", "false");
            return m;
        }
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    public static class CommentStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = -7063672993980857830L;

        @Inject
        private transient ItemCommentStep step;

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
            String note = step.getNote();
            Boolean isCommentAttachment = Boolean.parseBoolean(step.getLogfile());
            SprintsWebHook sprint = SprintsWebHook.getInstanceForAddComment(run, listener, prefix, note, isCommentAttachment);
            if (prefix.matches(Util.SPRINTSANDITEMREGEX)) {
                obj = sprint.addComment();
            }
            if (obj != null) {
                JSONObject respObj = new JSONObject(obj.toString());
                JSONArray ja = respObj.optJSONArray("COMMENT_ADDED_ITEM");
                if (respObj.has("status") && respObj.optString("status").equalsIgnoreCase("success")
                        && ja != null && ja.length() > 0) {
                    listener.getLogger().println(sprintsLogparser("Comment updated for --> " + ja.toString()));
                } else {
                    listener.error(sprintsLogparser("Comment not update due to Project/Sprint Permission"));
                }
            }
            return null;
        }
    }
}
