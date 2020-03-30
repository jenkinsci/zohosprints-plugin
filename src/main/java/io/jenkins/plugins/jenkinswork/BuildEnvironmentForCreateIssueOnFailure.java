package io.jenkins.plugins.jenkinswork;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.matrix.MatrixRun;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.Result;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.util.Util;
import jenkins.tasks.SimpleBuildWrapper;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang.StringUtils.isEmpty;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class BuildEnvironmentForCreateIssueOnFailure extends SimpleBuildWrapper {
    private static  final Logger LOGGER = Logger.getLogger(BuildEnvironmentForCreateIssueOnFailure.class.getName());
    private  String name = null , prefix = null, description = null, type = null, assignee = null, attachment = null;
    private static final String DEFAULT_NAME = "Jenkins Build issue";

    /**
     *
     * @param name Name of the Issue
     * @param prefix To where the item to be created {prefix} Backlog/sprint
     * @param description Description of the Sprints Item
     * @param type Type of the Sprints Type
     * @param assignee Assignee of the Sprint Item
     * @param attachment Attachment for the Sprint Item
     */
    @DataBoundConstructor
    public BuildEnvironmentForCreateIssueOnFailure(String name, String prefix, String description,
                                                   String type, String assignee, String attachment) {
        this.name = defaultIfEmpty(name, DEFAULT_NAME);
        this.description = description;
        this.prefix = prefix;
        this.type = type;
        this.assignee = assignee;
        this.attachment = attachment;
    }

    /**
     *
     * @return Name of the Issue
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
     * @param context BuildWrapper context
     * @param build Current Build Object
     * @param workspace File path of the Workspace
     * @param launcher Responsible for inherit the Global Variable
     * @param listener Listener Objetc of Task
     * @param initialEnvironment Environmental Variables
     * @throws InterruptedException when a thread that is sleeping, waiting, or is occupied is interrupted
     * @throws IOException Input/Output error
     */
    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher,
                      TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {
        String issueName = null;
        String issueDescription = null;
        String issuePrefix = null;
        String issueAssignee =  null;
        try {
            issueName = build.getEnvironment(listener).expand(name);
            issueAssignee = build.getEnvironment(listener).expand(assignee);
            issueDescription = build.getEnvironment(listener).expand(description);
            issuePrefix = build.getEnvironment(listener).expand(prefix);
            if (isEmpty(issuePrefix)) {
                throw new IllegalArgumentException("No project Prefix specified");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "unable to create Variables for issue create", e);
            if (listener instanceof BuildListener) {
                ((BuildListener) listener).finished(Result.FAILURE);
            }
        }
        final Map<String, String> envMap = new HashMap<>();
        envMap.put("SPRINTS_ISSUE_NAME", issueName);
        envMap.put("SPRINTS_ISSUE_DESCRIPTION", issueDescription);
        envMap.put("SPRINTS_ISSUE_PREFIX", issuePrefix);
        envMap.put("SPRINTS_ISSUE_ASSIGNEE", issueAssignee);
        envMap.put("SPRINTS_ISSUE_TYPE", type);
        envMap.put("SPRINTS_ISSUE_ATTACHMENT", attachment);
        envMap.put("SPRINTS_ISSUE_BUILD_ENVIRONMENT_AVAILABLE", Boolean.toString(true));
        context.getEnv().putAll(envMap);
    }

    /**
     *
     * @return Descriptor of this Class
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
    /**
     *@author selvavignesh.m
     * @version 1.0
     */
    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        /**
         *
         * @param item project object
         * @return if Sprints plugin Authendicated the true, or false
         */
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            if (Util.isAuthendicated() && ! (item instanceof MatrixProject)) {
                return true;
            } else {
                return false;
            }

        }

        /**
         *
         * @return Display Name of the Build ennvironmanatal pace
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.issue_in_failure();
        }

        /**
         *
         * @param prefix  To where the item to be created {prefix} Backlog/sprint
         * @return if prefix matches the regex then true else error message
         */
        public FormValidation doCheckPrefix(@QueryParameter final String prefix) {
            if (prefix.matches(Util.SPRINTSANDITEMREGEX)) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.prefix_message());
        }

        /**
         *
         * @param name Name of the Issue
         * @return if name is not empty or null then Ok else error
         */
        public FormValidation doCheckName(@QueryParameter final String name) {

           if (!name.isEmpty()) {
               return FormValidation.ok();
           }
           return FormValidation.error(Messages.item_name_message());
        }

        /**
         *
         * @param description Description of the Sprints Item
         * @return if description is not empty or null then Ok else error
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
         * @return if Assignee is not empty or null then Ok else error
         */
        public FormValidation doCheckAssignee(@QueryParameter final String assignee) {
            if (!assignee.isEmpty()) {
                return FormValidation.ok();
            }
            return FormValidation.error(Messages.mail_message());
        }

        /**
         *
         * @return LisBoxModel Object of Sprint ItemType
         */
        public ListBoxModel doFillTypeItems() {
            ListBoxModel m = new ListBoxModel();
            return m.add("Bug").add("Task");
        }

        /**
         *
         * @return  LisBoxModel Object of Sprint Item Attachment
         */
        public ListBoxModel doFillAttachmentItems() {
            ListBoxModel m = new ListBoxModel();
            m.add("Add Log File", "true");
            m.add("No don't add", "false");
            return m;
        }

        /**
         *
         * @param req request obj
         * @param json Object which contains values and key
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
