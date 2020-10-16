package io.jenkins.plugins.jenkinswork.postbuild;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixAggregatable;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
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
public class CreateSprintsItem extends Recorder implements MatrixAggregatable {
    private static  final Logger LOGGER = Logger.getLogger(CreateSprintsItem.class.getName());
    private String name = null , prefix = null, description = null, type = null, assignee = null, attachment = null;

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
    public CreateSprintsItem(String name, String prefix, String description,
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
     * @return Name of the Sprints Item
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return To where the item to be created {prefix} Backlog/sprint
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @return Description of the Sprints Item
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return Type of the Sprints Type
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
     *
     * @return Monitoring Service for BuildStep
     */
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    //This method will run this action only in Matrix parent job
    public MatrixAggregator createAggregator(MatrixBuild matrixbuild,
                                             Launcher launcher, BuildListener buildlistener) {
        return new MatrixAggregator(matrixbuild, launcher, buildlistener) {
            @Override
            public boolean endBuild() throws InterruptedException, IOException {
                LOGGER.log(Level.FINE, "end build of {0}", this.build.getDisplayName());
                return CreateSprintsItem.this._perform(this.build, this.launcher, this.listener);
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
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if(build instanceof MatrixRun) {
            return true;
        }
        return _perform(build, launcher, listener);
    }

    private boolean _perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return SprintsWorkAction.getInstanceForItemCreate(build, listener, SprintsWorkAction.POST_BUILD_TYPE, prefix, name,
                description, type, assignee, Boolean.parseBoolean(attachment)).createItem();
    }

    /**
     *
     * @return instance of DescriptorImpl
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        /**
         *
         * @param jobType Project Object
         * @return All jobs able to use this plugin
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         *
         * @return Display Name of the Action
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.create_item();
        }

        /**
         *
         * @param prefix  To where the item to be created {prefix} Backlog/sprint
         * @return if prefix matches the regex the OK else Error
         */
        public FormValidation doCheckPrefix(@QueryParameter final String prefix) {
            if (prefix.matches(Util.ADD_ITEM_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message(null));
        }

        /**
         *
         * @param name Name of the Sprints Item
         * @return if param is not null or empty then OK else Error
         */
        public FormValidation doCheckName(@QueryParameter final String name) {

            if (!name.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.item_name_message("Item"));
        }

        /**
         *
         * @param description Description of the Sprints Item
         * @return if param is not null or empty then OK else Error
         */
        public FormValidation doCheckDescription(@QueryParameter final String description) {
            if (!description.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.description_message());
        }

        /**
         *
         * @param assignee Assignee of the Sprint Item
         * @return if param is not null or empty then OK else Error
         */
        public FormValidation doCheckAssignee(@QueryParameter final String assignee) {
            if (!assignee.isEmpty() && assignee.matches(Util.MAIL_REGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.mail_message());
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

        /**
         *
         * @param req stapler request Object
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
