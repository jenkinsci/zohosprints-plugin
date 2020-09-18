package io.jenkins.plugins.sprints;

import hudson.matrix.MatrixRun;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import io.jenkins.plugins.configuration.SprintsConfig;
import io.jenkins.plugins.configuration.SprintsConnectionConfig;
import io.jenkins.plugins.util.Util;
import jenkins.model.Jenkins;
import org.json.JSONObject;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static io.jenkins.plugins.util.Util.sprintsLogparser;

public class Release {
    private static final Logger LOGGER = Logger.getLogger(Release.class.getName());
    private String projPrefix = null, releasePrefix = null, itemtoAssociatePrefix = null, owner = null, stage = null, releaseName = null, description = null;
    private Integer releaseTime = 0;
    private AbstractBuild<?, ?> build;
    Map<String, Object> paramMap = new HashMap<>();
    private SprintsConfig config;
    private Run<?, ?> run;
    private BuildListener listener;
    public Release setProjPrefix(String projPrefix) {
        this.projPrefix = Util.expandContent(build, listener, projPrefix);
        return this;
    }
    public Release setReleaseName(String releaseName) {
        this.releaseName =  Util.expandContent(build, listener, releaseName);
        return this;
    }

    public Release setDescription(String description) {
        this.description =  Util.expandContent(build, listener, description);
        return this;
    }
    public Release setReleasePrefix(String releasePrefix) {
        this.releasePrefix = releasePrefix;
        return this;
    }

    public Release setItemtoAssociatePrefix(String itemtoAssociatePrefix) {
        if(!isEmpty(itemtoAssociatePrefix)) {
            this.itemtoAssociatePrefix =  Util.expandContent(build, listener, itemtoAssociatePrefix);
        }
        return this;
    }

    public Release setOwner(String owner) {
        this.owner =  Util.expandContent(build, listener, owner);
        return this;
    }

    public Release setStage(String stage) {
        this.stage =  Util.expandContent(build, listener, stage);
        return this;
    }

    public Release setReleaseTime(String releaseTime) {
        this.releaseTime =  Integer.parseInt(Util.expandContent(build, listener, releaseTime));
        return this;
    }

    private Release(AbstractBuild<?,?> abstractBuild, BuildListener buildListener) {
        this.build = abstractBuild;
        this.listener = buildListener;
        this.run = abstractBuild;
    }
    private Release(){}
    public static Release getInstanceForCreate(final AbstractBuild<?,?> abstractBuild, final BuildListener listener, final String projPrefix,
                                               final String itemtoAssociatePrefix, final String releasename, String stage,
                                               final String descripttion, final String owner, final String period) {
        return new Release(abstractBuild, listener)
                .setProjPrefix(projPrefix)
                .setItemtoAssociatePrefix(itemtoAssociatePrefix)
                .setReleaseName(releasename)
                .setDescription(descripttion)
                .setStage(stage)
                .setOwner(owner)
                .setReleaseTime(period)
                .getCommonParam();

    }

    public static Release getInstanceForAssociateItems(final AbstractBuild<?,?> abstractBuild, final BuildListener listener, final String releasePrefix,
                                                       final String itemtoAssociatePrefix) {
        return new Release(abstractBuild, listener)
                .setReleasePrefix(releasePrefix)
                .setItemtoAssociatePrefix(itemtoAssociatePrefix)
                .getCommonParam();
    }

    public static Release getInstanceForUpdateStage(final AbstractBuild<?,?> abstractBuild, final BuildListener listener, final String releasePrefix,
                                                       final String stageToUpdate) {
        return new Release(abstractBuild, listener)
                .setReleasePrefix(releasePrefix)
                .setStage(stageToUpdate)
                .getCommonParam();
    }

    private boolean isProperProjectPrefix() {
        if(!isEmpty(projPrefix) && projPrefix.matches(Util.PROJECT_REGEX)) {
            return true;
        }
        listener.getLogger().println(parseLogMessage("Given Project prefix is wrong", true));
        return false;
    }
    private boolean isProperReleasePrefix() {
        if(!isEmpty(releasePrefix) && releasePrefix.matches(Util.RELEASE_REGEX)) {
            return true;
        }
        listener.getLogger().println(parseLogMessage("Given Release prefix is wrong", true));
        return false;
    }

    private boolean isProperItemPrefix() {
        if(isEmpty(itemtoAssociatePrefix)){
            return true;
        }
        else if(itemtoAssociatePrefix.matches(Util.ITEM_REGEX)) {
            return true;
        }
        listener.getLogger().println(parseLogMessage("Given Item prefix is wrong", true));
        return false;
    }
    private boolean isOwnerNULLorEmpty() {
        if(!isEmpty(owner)){
            return true;
        }
        listener.getLogger().println(parseLogMessage("Release Owner should not be empty", true));
        return false;
    }
    private String parseLogMessage(final String message, final boolean isError) {
        return sprintsLogparser(message, isError);
    }

    private Release getCommonParam() {
        String jobName;
        if(run instanceof MatrixRun) {
            jobName =  ((MatrixRun)run).getParentBuild().getProject().getFullName();
        } else {
            jobName = run.getParent().getFullName();
        }
        paramMap.put("name", jobName);
        paramMap.put("number", run.getId());
        paramMap.put("jenkinuser", Util.getBuildTriggererUserId(run));
        return this;
    }
    private RequestClient getClient(final String method) {
        doGetConfig();
        RequestClient client = new RequestClient(config.getReleaseAction(), method, paramMap);
        client.setOAuthHeader();
        return client;
    }
    private void doGetConfig() {
        if(config != null) {
            return;
        }
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        config = conf.getClient();
    }
    public boolean create() {
        try {
            PrintStream log = listener.getLogger();
            if(!isProperProjectPrefix() || !isProperItemPrefix() || !isOwnerNULLorEmpty()) {
                return false;
            } else if (releaseTime < 3) {
                listener.getLogger().println(parseLogMessage("Release time should not be less then 3 days", true));
                return false;
            }
            paramMap.put("stage", stage);
            paramMap.put("releasename", releaseName);
            paramMap.put("prefix", URLEncoder.encode(projPrefix, RequestClient.CHARSET));
            paramMap.put("action", "create");
            paramMap.put("releasename", releaseName);
            paramMap.put("period", releaseTime);
            paramMap.put("owner", owner);
            if(!isEmpty(itemtoAssociatePrefix)){
                paramMap.put("itemprefix", URLEncoder.encode(itemtoAssociatePrefix, RequestClient.CHARSET));
            }
             String respose = getClient(RequestClient.METHOD_POST).execute();
            if(!isEmpty(respose)) {
                JSONObject respObj = new JSONObject(respose);
                if(respObj.has("status") && respObj.getString("status").equals("success")){
                    log.println(parseLogMessage("Release Create Successfully", false));
                    if(respObj.has("mappeditem")){
                        log.println(parseLogMessage("Associated Item(s) : "+ respObj.getJSONArray("mappeditem"), false));
                    }
                } else {
                    log.println(parseLogMessage(respObj.getString("message"), true));
                }
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "", e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }
        return false;
    }

    public boolean associateItem() {
        try {
            PrintStream log = listener.getLogger();
            if(!isProperItemPrefix() || !isProperReleasePrefix()) {
                return false;
            }
            paramMap.put("action", "associateitem");
            paramMap.put("prefix", URLEncoder.encode(releasePrefix, RequestClient.CHARSET));
            paramMap.put("itemprefix", URLEncoder.encode(itemtoAssociatePrefix, RequestClient.CHARSET));
            String response = getClient(RequestClient.METHOD_POST).execute();
            if(!isEmpty(response)) {
                JSONObject respObj = new JSONObject(response);
                if(respObj.has("status") && respObj.getString("status").equals("success")){
                    log.println(parseLogMessage("Items are associated", false));
                    log.println(parseLogMessage("Associated Item(s) : "+ respObj.getJSONArray("mappeditem"), false));
                } else {
                    log.println(parseLogMessage(respObj.getString("message"), true));
                }
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "", e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }
        return false;
    }

    public boolean updateReleaseStage() {
        try {
            PrintStream log = listener.getLogger();
            if(!isProperItemPrefix() || !isProperReleasePrefix()) {
                return false;
            }
            paramMap.put("action", "updatestage");
            paramMap.put("prefix", URLEncoder.encode(releasePrefix, RequestClient.CHARSET));
            paramMap.put("stage", stage);
            String respose = getClient(RequestClient.METHOD_POST).execute();
            if(!isEmpty(respose)) {
                JSONObject respObj = new JSONObject(respose);
                if(respObj.has("status") && respObj.getString("status").equals("success")){
                    log.println(parseLogMessage("Stage updated", false));
                } else {
                    log.println(parseLogMessage("Release Stage not updated", false));
                    log.println(parseLogMessage(respObj.getString("message"), true));
                }
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "", e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }
        return false;
    }
}
