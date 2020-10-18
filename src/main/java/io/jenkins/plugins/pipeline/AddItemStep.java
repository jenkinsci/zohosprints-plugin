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
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static io.jenkins.plugins.util.Util.sprintsLogparser;

/**
 * AddItemStep used to Create sprint item  from pipeline job.
 *
 * @author selvavignesh.m
 * @version 1.0
 */
public class AddItemStep extends AbstractStepImpl {
    private  String name = null, prefix = null, description = null, type = null, assignee = null, attachment = null;

    /**
     *
     * @param name Name of the Sprints Item
     * @param prefix To where the item to be created {prefix} Backlog/sprint
     * @param description Description of the Sprints Item
     * @param type Type of the Sprints Type
     * @param assignee Assignee of the Sprint Item
     * @param attachment Attachment for the Sprint Item
     */
    @DataBoundConstructor
    public AddItemStep(String name, String prefix, String description,
                       String type, String assignee, String attachment) {
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.type = type;
        this.assignee = assignee;
        this.attachment = attachment;
    }

    /**
     *
     * @return Attachment String
     */
    public String getAttachment() {
        return attachment;
    }
    /**
     *
     * @return Name of the Sprint Item
     */
    public String getName() {
        return name;
    }
    /**
     *
     * @return To where Sprint Item to get created {prefix} backlog/sprint
     */
    public String getPrefix() {
        return prefix;
    }
    /**
     *
     * @return Dwcription of the Sprint Item
     */
    public String getDescription() {
        return description;
    }
    /**
     *
     * @return Type of the Sprint item
     */
    public String getType() {
        return type;
    }
    /**
     *
     * @return Assignee of the Sprint Item
     */
    public String getAssignee() {
        return assignee;
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension(optional = true)
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {
        public DescriptorImpl() {
            super(AddItemExecutor.class);
        }

        /**
         *
         * @return function name of the add Sprint item Action
         */
        @Override
        public String getFunctionName() {
            return "sprintsAddItem";
        }

        /**
         *
         * @return Display name in the UI
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.create_item();
        }

        /**
         *
         * @return ListBoxModel of Sprint itemType
         */
        public ListBoxModel doFillTypeItems() {
            ListBoxModel m = new ListBoxModel();
            return m.add("Bug").add("Task");
        }

        /**
         *
         * @return ListBoxModel data of Sprint Item Attachment option
         */
        public ListBoxModel doFillAttachmentItems() {
            ListBoxModel m = new ListBoxModel();
            m.add("Add Log File", "true");
            m.add("No dont add", "false");
            return m;
        }

    }
    
    /**
     * @version 1.0
     */
    public static class AddItemExecutor
            extends AbstractSynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = 4156687831425261546L;
        @Inject
        private transient AddItemStep step;
        @StepContextParameter
        private transient Run<?, ?> run;
        @StepContextParameter
        private transient TaskListener listener;

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
            try {
               // String mailid = Util.getCurrentUserMailId(run);
                Boolean isItemAddAttachment = Boolean.parseBoolean(step.getAttachment());
                SprintsWebHook createItem = SprintsWebHook.getInstanceForCreateItem(run, listener, step.getPrefix(), step.getName(),
                        step.getDescription(), step.getType(), step.getAssignee(), isItemAddAttachment);
                String resp = createItem.createItem();
                Object respObject = Util.parseResponse(resp, "status");
                if (respObject != null && respObject.toString().equals("success")) {
                    listener.getLogger().println(Util.sprintsLogparser("Item created", false));
                } else {
                    listener.getLogger().println(sprintsLogparser("Item not created", true));
                }
            } catch (Exception e) {
                listener.error(sprintsLogparser("Error Occured. Item not created", true));
            }
            return null;
        }
    }
}
