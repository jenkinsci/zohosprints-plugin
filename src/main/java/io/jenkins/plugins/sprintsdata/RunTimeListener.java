package io.jenkins.plugins.sprintsdata;

import hudson.EnvVars;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.matrix.MatrixRun;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import io.jenkins.plugins.configuration.SprintsConfig;
import io.jenkins.plugins.configuration.SprintsConnectionConfig;
import io.jenkins.plugins.sprints.AttachmentUtil;
import io.jenkins.plugins.sprints.RequestClient;
import io.jenkins.plugins.sprints.SprintsWebHook;
import io.jenkins.plugins.util.Util;
import jenkins.model.Jenkins;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.util.Util.sprintsLogparser;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
@Extension
public class RunTimeListener extends RunListener<Run<?, ?>> {
    private static final Logger LOGGER = Logger.getLogger(RunTimeListener.class.getName());

    /**
     *
     * @param run Run object of the running build
     * @param listener Running Build's listener Object of the task
     */
    @Override
    public void onStarted(Run<?, ?> run, TaskListener listener) {
        LOGGER.log(Level.INFO,"Job {0} - #{1} Started", new Object[]{run.getParent().getFullName(), run.getNumber()});
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        SprintsConfig zspojo = conf.getClient();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-ZS-JENKINS-ID", conf.getZsheader());
        if (Util.isAuthendicated() && checkBuildTypeForUpdate(run)) {
            Map<String, Object> buildDatamap = new HashMap<>();
            ExtensionList<QueueTimeListener> extensionList = Jenkins.getInstance().getExtensionList(QueueTimeListener.class);
            if (extensionList != null && !extensionList.isEmpty()) {
                QueueTimeListener queueTimeListener = extensionList.get(0);
                buildDatamap.put("queuetime", queueTimeListener.getTimeInQueue());
            }
            buildDatamap.put("name", run.getParent().getFullName());
            buildDatamap.put("action", "createbuild");
            //buildDatamap.put("mailid", zspojo.getMailid());
            buildDatamap.put("number", run.getNumber());
            buildDatamap.put("starttime", run.getStartTimeInMillis());
            buildDatamap.put("jenkinuser", Util.getBuildTriggererUserId(run));
            try {
                RequestClient client = new RequestClient(zspojo.getCreateBuild(), RequestClient.METHOD_POST, buildDatamap, headerMap);
                client.setOAuthHeader();
                client.execute();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "", e);
            }
        }
    }

    /**
     *
     * @param run Run object of the Deleted build
     */
    @Override
    public void onDeleted(Run<?, ?> run) {
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        SprintsConfig zspojo = conf.getClient();
        if (Util.isAuthendicated() && checkBuildTypeForUpdate(run)) {
            Map<String, Object> buildDatamap = new HashMap<>();
            buildDatamap.put("action", "delete");
            buildDatamap.put("name", run.getParent().getFullName());
            buildDatamap.put("number", run.getNumber());
            RequestClient client = new RequestClient(zspojo.getDeleteBuild(), RequestClient.METHOD_DELETE, buildDatamap);
            try {
                client.setOAuthHeader();
                client.execute();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "", e);
            }
        }
    }

    /**
     *
     * @param run Run object of the Finalised build
     */
    @Override
    public void onFinalized(Run<?, ?> run) { }


    /**
     *
     * @param run Run object of the Completed build
     * @param listener Completed Build's listener Object of the task
     */
    @Override
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public void onCompleted(final Run<?, ?> run, @Nonnull final TaskListener listener) {
        try {
            EnvVars envVars = run.getEnvironment(listener);
            Boolean isIssueCreateConfigured = envVars.containsKey("SPRINTS_ISSUE_BUILD_ENVIRONMENT_AVAILABLE");

            if (isIssueCreateConfigured && Result.FAILURE.equals(run.getResult())) {
                    String name = envVars.get("SPRINTS_ISSUE_NAME");
                    String description = envVars.get("SPRINTS_ISSUE_DESCRIPTION");
                    String assignee = envVars.get("SPRINTS_ISSUE_ASSIGNEE");
                    String type = envVars.get("SPRINTS_ISSUE_TYPE");
                    boolean isLogAvailable = Boolean.valueOf(envVars.get("SPRINTS_ISSUE_ATTACHMENT"));
                    String prefix = envVars.get("SPRINTS_ISSUE_PREFIX");
                    String resp = SprintsWebHook.getInstanceForCreateItem(run, listener, prefix, name, description,
                                                                    type, assignee, isLogAvailable).createItem();
                    Object respObject = Util.parseResponse(resp, "status");
                    if (respObject != null && respObject.toString().equals("success")) {
                        listener.getLogger().println(sprintsLogparser("Item created on Build Failure"));
                    } else {
                        listener.getLogger().println(sprintsLogparser("Item not created on Build Failure"));
                    }
            }

            if (Util.isAuthendicated() && checkBuildTypeForUpdate(run)) {
                List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
                SprintsConnectionConfig conf = extnList.get(0);
                SprintsConfig api = conf.getClient();
              //  TaskListener listener = TaskListener.NULL;
                //  new Thread(() -> {
                LOGGER.log(Level.INFO,"Job {0} - #{1} Ended", new Object[]{run.getParent().getFullName(), run.getNumber()});
                Map<String, Object> buildDatamap = new HashMap<>();
                ExtensionList<QueueTimeListener> extensionList = Jenkins.getInstance().getExtensionList(QueueTimeListener.class);
                if (extensionList != null && !extensionList.isEmpty()) {
                    QueueTimeListener queueTimeListener = extensionList.get(0);
                    buildDatamap.put("queuetime", queueTimeListener.getTimeInQueue());
                }
                buildDatamap.put("name", run.getParent().getFullName());
                buildDatamap.put("number", run.getNumber());
                buildDatamap.put("starttime", run.getStartTimeInMillis());
                buildDatamap.put("jenkinuser", Util.getBuildTriggererUserId(run));
                buildDatamap.put("duration", run.getDuration());

                buildDatamap.put("result", run.getResult().toString());
                buildDatamap.put("estimatedduration", run.getEstimatedDuration());
                HashMap<String, AttachmentUtil> buildLogMap = new HashMap<>();
                buildLogMap.put("uploadfile", new AttachmentUtil(run,listener));
                String branch = expandContent(run, listener);
                if (branch != null) {
                    buildDatamap.put("branch", branch);
                }
                try {
                    RequestClient client = new RequestClient(api.getBuildPush(), RequestClient.METHOD_POST, buildDatamap);
                    client.setAttachment(buildLogMap);
                    client.setOAuthHeader();
                    client.execute();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING,  "", e);
                }
                //  }).start();
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }
        /* SPrints Data push Action*/

    }

    /**
     *
     * @param run Run object of the build
     * @return is Build is Matrix type?
     */
    private boolean checkBuildTypeForUpdate(final Run<?, ?> run){
        if (run instanceof MatrixRun) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param run Run object of the Completed build
     * @param listener Listener Object of the build
     * @return get the branch details of the build depends on the scm
     */
    private  String expandContent(final Run<?, ?> run, final TaskListener listener) {
        String value = null;
        try  {
            Util.Branchdetails[] bDetails = Util.Branchdetails.values();
            for (int sv = 0; sv < bDetails.length; sv++) {
                String key = bDetails[sv].getVariable();
                value = run.getEnvironment(listener).expand(key);
                if (!key.equals(value)) {
                    break;
                } else {
                    value = null;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
            return null;
        }
        return value;
    }

}
