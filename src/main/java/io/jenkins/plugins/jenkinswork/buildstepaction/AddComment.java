package io.jenkins.plugins.jenkinswork.buildstepaction;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
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
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class AddComment extends Builder {

    private static  final Logger LOGGER = Logger.getLogger(AddComment.class.getName());


    private String prefix;
   // private String onfailure;

    private String note;

    private String logfile;
    private AbstractBuild<?, ?> abstrctBuild;

    /**
     *
     * @return Comment Attachment String
     */
    public String getLogfile() {
        return logfile;
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
     * @return Comment to be added in Sprint Item
     */
    public String getNote() {
        return note;
    }

    /**
     *
     * @param prefix Comment Attachment String
     * @param note Comment to be added in Sprint Item
     * @param logfile In which Sprint Item comment to be added
     */
    @DataBoundConstructor
    public AddComment(String prefix, String note, String logfile) {
        this.prefix = prefix;
        this.note = note;
        this.logfile = logfile;
    }

    /**
     *
     * @param build Build Object of Current build
     * @param launcher launcher is responsible for inheriting environment variables
     * @param listener Receives events that happen during a build
     * @return if build success then true, else false
     * @throws UnsupportedEncodingException
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) throws UnsupportedEncodingException {
        return SprintsWorkAction.getInstanceForAddComment(build, listener, SprintsWorkAction.BUILD_TYPE,
                prefix, note, Boolean.parseBoolean(logfile)).addcomment();
    }

    /**
     *
     * @return instance of DescriptorImpl
     */
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
         * @param jobType Project Object
         * @return All jobs able to use this plugin
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        /**
         * Prefix valid check
         * @param prefix In which Sprint Item comment to be added
         * @return if prefix matches the regex the OK else Error
         */
        public FormValidation doCheckPrefix(@QueryParameter final String prefix) {

            if (prefix.matches(Util.SPRINTSANDITEMREGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        /**
         * comments valid check
         * @param note Comment to be added in Sprint Item
         * @return if param is not null or empty then OK else Error
         */
        public FormValidation doCheckNote(@QueryParameter final String note) {

            if (!note.isEmpty()) {
                return FormValidation.ok();
            }
            return  FormValidation.error(Messages.comment_message());
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

        /**
         *
         * @param req Stapler request object
         * @param json Contains value and key
         * @return
         */
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) {
            req.bindJSON(this, json);
            save();
            return true;
        }

        /**
         *
         * @return Display Name of Action
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.add_comment();
        }

    }
}
