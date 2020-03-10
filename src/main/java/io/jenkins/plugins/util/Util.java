package io.jenkins.plugins.util;

import hudson.EnvVars;
import hudson.ProxyConfiguration;
import hudson.model.*;
import hudson.model.Cause.UserIdCause;
import hudson.tasks.Mailer;
import hudson.triggers.SCMTrigger;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.configuration.SprintsConfig;
import io.jenkins.plugins.configuration.SprintsConnectionConfig;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class Util {

    private static final Logger LOGGER = Logger.getLogger(Util.class.getName());
    private static final String BUILD_LOG_COMMENT_VAR = "$SPRINTS_BUILD_LOG";
    public static final String SPRINTSANDITEMREGEX = "^P[0-9]+#(I|S)[0-9]+(:?,P[0-9]+#(I|S)[0-9]+)*$";
    public static final String PROJECT_REGEX = "^(P[0-9]+)$";
    public static final String ADD_ITEM_REGEX = "^P[0-9]+(#S[0-9]+)*$";
    public static final String ITEM_REGEX = "(^P[0-9]+#I[0-9]+(?:,P[0-9]+#I[0-9]+)*)$";
    private static final String PLUGIN_RESOUCE_PATH = "/plugin/zohosprints/";
    //public static final String MAIL_REGEX = "^([a-zA-Z0-9]([\\w\\-\\.\\+\\']*)@([\\w\\-\\.]*)(\\.[a-zA-Z]{2,20}(\\.[a-zA-Z]{2}){0,2}))$"

    public  enum Branchdetails {
        GITHUB("$GIT_LOCAL_BRANCH"), GITLAB("$gitlabBranch"), BITBUCKET("$BRANCH_NAME"), SPRINTS("$SPRINTS_BRANCH");
        private final String variable;
        private static Branchdetails[] branchdetails = values();
        Branchdetails(String branch) {
            this.variable = branch;
        }
        public String getVariable() {
            return this.variable;
        }
        public static Branchdetails getValue(String variable) {
            for (Branchdetails bDetails : branchdetails) {
                if (bDetails.getVariable().equalsIgnoreCase(variable)) {
                    return bDetails;
                }
            }
            return null;
        }
    }


    /**
     *
     * @param value value to be validated
     * @return isForm is Valid
     */
    public static FormValidation valueCheck(String value) {
        if(value.isEmpty()) {
            return FormValidation.error("Seems no value");
        } else if(StringUtils.isAlphanumeric(value) || value.contains("$")) {
            return FormValidation.ok();
        } else {
            return FormValidation.error("wrong value! enter the proper one");
        }
    }

    /**
     *
     * @return ListBoxModel of all details
     */
    public static ListBoxModel getOnFailureCase() {
        ListBoxModel model = new ListBoxModel();
        model.add("Don't do anything", "false");
        model.add("Mark build as Failure", "true");
        return model;
    }

    /**
     *
     * @param build Abstarct build Object
     * @param listener BuildListner Object of Build
     * @param value value to be checked
     * @return true/false
     */
    public static boolean markBuildAsFailed(final AbstractBuild<?, ?> build, final BuildListener listener, final String value) {
        if (value == null) {
            build.setResult(Result.FAILURE);
            listener.getLogger().println("Specified variable not available. So, marking build as Failure");
            return false;
        }
        return true;
    }

    /**
     *
     * @param build Abstarct build Object of build
     * @param notes key to be get original Value
     * @return original value of the Key
     */
    @Deprecated
    public static String expandContent(AbstractBuild<?, ?> build, String notes) {
        EnvVars envVars = new EnvVars();
        envVars.putAll(build.getEnvVars());
        envVars.putAll(build.getBuildVariables());
        String value = envVars.expand(notes);
       // if(value.equals(notes)){throw new IllegalArgumentException("No key "+notes+" available");}
        return value;
    }

    /**
     *
     * @param build Abstarct build Object of Build
     * @param listener Listener of build
     * @param key key to be get original Value
     * @return original value of the Key
     */
    public static  String expandContent(final AbstractBuild<?, ?> build, final BuildListener listener, final String key) {
        String value = null;
        try  {
           value = build.getEnvironment(listener).expand(key);
           if (isEmpty(value)) {
               throw new IllegalArgumentException("No Key " + key + " available");
           }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
                listener.finished(Result.FAILURE);
        }
    return value;
    }

    /**
     *
     * @param resp json string
     * @param key key need to be get
     * @return Parsed Reponse
     */
    public static Object parseResponse(final String resp, final String key) {
        if (resp != null && !resp.isEmpty()) {
            // LOGGER.info(resp);
            try {
                JSONObject json = new JSONObject(resp);
                if (((json.has("STATUS") && json.getString("STATUS").equals("success")) || (json.has("status") && json.getString("status").equals("success"))) && json.has(key)) {

                    return json.get(key);
                }

            } catch (JSONException e) {
                //  LOGGER.log(Level.WARNING,"",e);
                return null;
            }
        }
        return null;
    }

    /**
     *
     * @param arr jsonarray
     * @return value from JSONArray
     */
    public static String getStringFromJSONArray(final JSONArray arr) {
        StringBuilder str = new StringBuilder();
        if (arr == null || arr.length() == 0) {
            return "";
        }
        for (int i = 0; i < arr.length(); i++) {
            str.append(arr.get(i));
        }
        return str.toString();
    }

    /**
     * While write a messge in console Log Zoho Sprints message alone higlighted with Product name
     * @param message Meesage to parse for Sprints Plugin
     * @return Prepend ZohoSprints in logger
     */
    public static String sprintsLogparser(final String message) {
        StringBuffer buffer = new StringBuffer("[Zoho Sprints] ");
        return buffer.append(message).toString();
    }
    /**
     * remove script related content for antisemy filter
     * @param str value to be removed for script content
     * @return removed value
     */
    public String removeScriptContent(String str) {
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("'", "&#39;");
        str = str.replaceAll("/", "&#x2F;");
        str = str.replaceAll("`", "&#x60;");
        return str.replaceAll("=", "&#x3D;");
    }

    /**
     * It will get Resourse path of Plugin
     * Path like "/plugin/<artifect-id>/"
     * @return plugin resource path
     */
    public static String getResourcePath() {
        return PLUGIN_RESOUCE_PATH;
    }

    /**
     *
     * @return If authendicated sprints.svg will return or sprints_icon.svg
     */
    public static String getSprintsIconByAuth() {
            return getResourcePath() + "sprints.svg";
    }

    /**
     *Get the Build triggerer mail id
     * @param run run Object if the Current build
     * @return triggerer mail id of currentbuild
     */
    @Deprecated
    public static String getCurrentUserMailId(final Run<?, ?> run) {
        String  buildtriggerer = getBuildTriggererUserId(run);
        return User.get(buildtriggerer).getProperty(Mailer.UserProperty.class).getAddress();
    }
    /**
     *  Get the Build triggerer mail id
     * @param build Abstract build Object of the build
     * @return  triggerer mail id of currentbuild
     */
    @Deprecated
    public static String getCurrentUserMailId(final AbstractBuild<?, ?> build) {
        String  buildtriggerer = getBuildTriggererUserId(build);
        return User.get(buildtriggerer).getProperty(Mailer.UserProperty.class).getAddress();
    }

    /**
     *
     * @return Sprints portal Admin Mail id
     */
    public static String getAdminMailid() {
        List<SprintsConnectionConfig> extnList = new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        return conf.getConnections().get(0).getMailid();
    }

    /**
     *
     * @param run run object of the build
     * @return triggerer of the build
     */
    public static String getBuildTriggererUserId(Run run) {
        Cause.UpstreamCause upstreamCause = (Cause.UpstreamCause) run.getCause(Cause.UpstreamCause.class);
        Run temp = null;
        while (upstreamCause != null) {
            Job job = Jenkins.getInstance().getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
            if (job != null) {
                temp = job.getBuildByNumber(upstreamCause.getUpstreamBuild());
                if (temp != null) {
                   upstreamCause = (Cause.UpstreamCause) temp.getCause(Cause.UpstreamCause.class);
                }
            }
        }
        if (temp != null) {
            run = temp;
        }
        SCMTrigger.SCMTriggerCause scmTriggerCause = (SCMTrigger.SCMTriggerCause) run.getCause(SCMTrigger.SCMTriggerCause.class);
        if (scmTriggerCause != null) {
            return "SCMTrigger";
        }
        UserIdCause cause = (UserIdCause) run.getCause(UserIdCause.class);
        if (cause == null) {
            return "System";
        }
        return cause.getUserId();
    }

    /**
     *
     * @return true/false {is Proxy configured in Jenkins}
     */
    public static boolean isProxyConfigured() {
        ProxyConfiguration proxy = Jenkins.getInstance().proxy;
        if (proxy != null) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return true/false {is Sprints plugin Authendicated}
     */
    public static boolean isAuthendicated() {
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        if (extnList.isEmpty()) {
            return false;
        }
        SprintsConnectionConfig conf = extnList.get(0);
        return conf.isMigrated();
    }

    /**
     *
     * @return SprintsConfig instance
     */
    @Restricted(NoExternalUse.class)
    public static SprintsConfig getSprintsGlobalConfig() {
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        return conf.getClient();
    }
}
