package io.jenkins.plugins.sprints;

import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.configuration.SprintsConfig;
import io.jenkins.plugins.configuration.SprintsConnectionConfig;
import io.jenkins.plugins.util.Util;
import jenkins.model.Jenkins;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static io.jenkins.plugins.util.Util.sprintsLogparser;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class SprintsWebHook {

    private static final Logger LOGGER = Logger.getLogger(SprintsWebHook.class.getName());

    private static final String ITEM_COMMENT_ACTION = "additemcomment";
    private static final String ITEM_UPDATE_STATUS_ACTION = "updatestatus";
    private static final String SPRINT_COMMENT_ACTION = "addsprintcomment";
    private static final String ITEM_UPDATE_PRIORITY = "updatepriority";
    private static final String ITEM_CREATE = "additem";
    private static final String ADD_FEED_STATUS = "feedstatus";
    private static final String GET_STATUS = "getstatus";

    private String prefix = null, status = null, notes = null, itemName = null, itemDesc = null, priority = null, assignee = null, itemType = null, mailId = null;
    private boolean isAttachmentNeeded = false;
    private HashMap<String, Object> paramMap = new HashMap<>();
    //private HashMap<String, String> headerMap = new HashMap<>();
    private HashMap<String, AttachmentUtil> attachMap = new HashMap<>();
    private List<String> prefixList = new ArrayList<>();
    private Run<?, ?> run;
    private BuildListener buildListener;
    private TaskListener taskListener;
    private SprintsConfig config;


    /**
     *
     * @return Prefix List
     */
    public List<String> getPrefixList() {
        return prefixList;
    }

    /**
     *
     * @param fromPrefixList prefix as List of String
     * @return instance of SprintsWebHook Class
     */
    public SprintsWebHook setPrefixList(final List<String> fromPrefixList) {
        this.prefixList = fromPrefixList;
        return this;
    }

    /**
     *
     * @return prefix as String
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @param fromPrefix prefix for the Action
     * @return Instance of Class
     */
    public SprintsWebHook setPrefix(final String fromPrefix) {
        this.prefix = fromPrefix;
        return this;
    }

    /**
     *
     * @return Status to be updated in Sprints Item/ Feed to be updated
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param fromStatus Status to update in Sprints Item/ Feed to  updated
     * @return Instance of Class
     */
    public SprintsWebHook setStatus(final String fromStatus) {
        this.status = fromStatus;
        return this;
    }

    /**
     *
     * @return Comment
     */
    public String getNotes() {
        return notes;
    }

    /**
     *
     * @param fromNotes Comment to be added in Sprints Item
     * @return Instance of Class
     */
    public SprintsWebHook setNotes(final String fromNotes) {
        this.notes = fromNotes;
        return this;
    }

    /**
     *
     * @return name of the Item
     */
    public String getItemName() {
        return itemName;
    }

    /**
     *
     * @param fromItemName Sprints Item Name
     * @return Instance of Class
     */
    public SprintsWebHook setItemName(final String fromItemName) {
        this.itemName = fromItemName;
        return this;
    }

    /**
     *
     * @return Description of the Sprints Item
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     *
     * @param fromItemDesc Description of the Sprints Item
     * @return Instance of Class
     */
    public SprintsWebHook setItemDesc(final String fromItemDesc) {
        this.itemDesc = fromItemDesc;
        return this;
    }

    /**
     *
     * @return Priority of Item
     */
    public String getPriority() {
        return priority;
    }

    /**
     *
     * @param fromPriority Priority to be updated for Sprints Item
     * @return Instance of Class
     */
    public SprintsWebHook setPriority(final String fromPriority) {
        this.priority = fromPriority;
        return this;
    }

    /**
     *
     * @return Assignee of Sprints Item
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     *
     * @param fromAssignee Assignee of the Sprints Item
     * @return Instance of Class
     */
    public SprintsWebHook setAssignee(final String fromAssignee) {
        this.assignee = fromAssignee;
        return this;
    }

    /**
     *
     * @return Type of the Item
     */
    public String getItemType() {
        return itemType;
    }

    /**
     *
     * @param fromItemType Type of Sprints Item Type to create
     * @return Instance of Class
     */
    public SprintsWebHook setItemType(final String fromItemType) {
        this.itemType = fromItemType;
        return this;
    }

    /**
     *
     * @return admin mailid of portal
     */
    public String getMailId() {
        return mailId;
    }

    /**
     *
     * @return Instance of Class
     */
    public SprintsWebHook setMailId() {
        this.mailId = config != null ? config.getMailid() : null;
        return this;
    }

    /**
     *
     * @return isAttachment added
     */
    public boolean isAttachmentNeeded() {
        return isAttachmentNeeded;
    }

    /**
     *
     * @param attachmentNeeded does api call need to add build log as attachment
     * @return Instance of Class
     */
    public SprintsWebHook setAttachmentNeeded(boolean attachmentNeeded) {
        isAttachmentNeeded = attachmentNeeded;
        if (isAttachmentNeeded) {
            setAttachMap();
        }
        return this;
    }

    /**
     *
     * @return api param map
     */
    public HashMap<String, Object> getParamMap() {
        return paramMap;
    }

    /**
     *
     * @param fromParamMap Api param Map
     * @return Instance of Class
     */
    public SprintsWebHook setParamMap(final HashMap<String, Object> fromParamMap) {
        this.paramMap = fromParamMap;
        return this;
    }

    /**
     *
     * @return api attachment file map
     */
    public HashMap<String, AttachmentUtil> getAttachMap() {
        return attachMap;
    }

    /**
     *
     * @return Instance of Class
     */
    public SprintsWebHook setAttachMap() {
        attachMap.put("uploadfile", new AttachmentUtil(getRun(), getTaskListener()));
        return this;
    }

    /**
     *
     * @return Run of the build
     */
    public Run<?, ?> getRun() {
        return run;
    }

    /**
     *
     * @param runObj Run Object of the Build
     */
    public void setRun(final Run<?, ?> runObj) {
        this.run = runObj;
    }

    /**
     *
     * @return returns events that happen during a build
     */
    public BuildListener getBuildListener() {
        return buildListener;
    }

    /**
     *
     * @param buildListener Receives events that happen during a build
     */
    public void setBuildListener(BuildListener buildListener) {
        this.buildListener = buildListener;
    }

    /**
     *
     * @return returns events that happen during a build
     */
    public TaskListener getTaskListener() {
        return taskListener;
    }

    /**
     *
     * @param taskListener Receives events that happen during a build
     * @return Instance of Class
     */
    public SprintsWebHook setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
        return this;
    }

    /**
     *
     * @return Instance of Class
     */
    private SprintsWebHook doInitParamMap() {
      //  doInitCommonParamMap();
        paramMap.put("name", run.getParent().getFullName());
        paramMap.put("number", run.getId());
        paramMap.put("jenkinuser", Util.getBuildTriggererUserId(run));
        return this;
    }

//    /**
//     *
//     * @return Instance of Class
//     */
//    private SprintsWebHook doInitCommonParamMap() {
//        paramMap.put("zapikey", config.getApiToken());
//        return this;
//    }

    /**
     *
     * @throws Exception Throws When an Runtime error Occurs
     */
    private SprintsWebHook() throws Exception {
        isAuthedicated();
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
       // headerMap.put("X-ZS-JENKINS-ID", conf.getZsheader());
        config = conf.getClient();
    }

    /**
     *
     * @param runObj Run object of the Build
     * @throws Exception Throws When an Runtime error Occurs
     */
    private SprintsWebHook (final Run<?, ?> runObj) throws Exception {
        isAuthedicated();
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
       // headerMap.put("X-ZS-JENKINS-ID", conf.getZsheader());
        config = conf.getClient();
        this.run = runObj;
    }

    /**
     *
     * @param fromrun Run Object of the Build
     * @param fromlistener listener Receives events that happen during a build
     * @param fromprefix For which sprint Item to create
     * @param fromitemName Name of the Sprints Item
     * @param fromitemDesc Sprints Item Description
     * @param fromitemType Sprints Item Type
     * @param fromitemAssignee Sprints Item Assignee
     * @param fromisAttachmentNeeded Need to Add build log as a Attachment while create the Sprints Item
     * @return Instance of Class
     * @throws Exception Throws When an Runtime error Occurs
     */
    public static SprintsWebHook getInstanceForCreateItem(final Run<?, ?> fromrun, final TaskListener fromlistener, final String fromprefix, final String fromitemName,
                                                          final String fromitemDesc, final String fromitemType, final String fromitemAssignee,
                                                          final boolean fromisAttachmentNeeded) throws Exception {
        return new SprintsWebHook(fromrun).setTaskListener(fromlistener).setPrefix(fromprefix).setItemName(fromitemName).setItemDesc(fromitemDesc)
                .setItemType(fromitemType).setAssignee(fromitemAssignee).setAttachmentNeeded(fromisAttachmentNeeded).setMailId().doInitParamMap().makeAddItemPrefix();
    }

    /**
     *
     * @param fromrun Run Object of the Build
     * @param fromlistener listener Receives events that happen during a build
     * @param fromprefix prefix for which Sprint Item Status to Update/ Feed status to be update
     * @param fromstatus Sprints Item Status/ Feed status to update
     * @param fromisFeedStatus is Insatnce for feed status
     * @return Instance of Class
     * @throws Exception Throws When an Runtime error Occurs
     */
    public static SprintsWebHook getInstatnceForUpdateStatus(final Run<?, ?> fromrun, final TaskListener fromlistener, final String fromprefix, final String fromstatus,
                                                             final boolean fromisFeedStatus) throws Exception {
        if (fromisFeedStatus) {
            return new SprintsWebHook(fromrun).setTaskListener(fromlistener).setPrefix(fromprefix).setStatus(fromstatus).setMailId().doInitParamMap().makeProjectPrefix();
        }
        return new SprintsWebHook(fromrun).setTaskListener(fromlistener).setPrefix(fromprefix).setStatus(fromstatus).setMailId().doInitParamMap().makeItemPrefix();
    }

    /**
     *
     * @param fromrun Run Object of the Build
     * @param fromlistener listener Receives events that happen during a build
     * @param fromprefix prefix for for which Sprint Item Priority to be Updated
     * @param frompriority priority to Update
     * @return Instance of Class
     * @throws Exception Throws When an Runtime error Occurs
     */
    public static SprintsWebHook getInstatnceForUpdatePriority(final Run<?, ?> fromrun, final TaskListener fromlistener, final String fromprefix, final String frompriority) throws Exception {
        return new SprintsWebHook(fromrun).setTaskListener(fromlistener).setPrefix(fromprefix).setPriority(frompriority).setMailId().doInitParamMap().makeItemPrefix();
    }

    /**
     *
     * @param fromrun Run Object of the Build
     * @param fromlistener listener Receives events that happen during a build
     * @param fromprefix prefix for in which Sprint Item Comment to Add
     * @param fromnotes Comment to add
     * @param fromisAttachmentNeeded is Build log need to add along with comment
     * @return Instance of Class
     * @throws Exception Throws When an Runtime error Occurs
     */
    public static SprintsWebHook getInstanceForAddComment(final Run<?, ?> fromrun, final TaskListener fromlistener, final String fromprefix,
                                                          final String fromnotes, final boolean fromisAttachmentNeeded) throws Exception {
        return new SprintsWebHook(fromrun).setTaskListener(fromlistener).setPrefix(fromprefix).setNotes(fromnotes).setAttachmentNeeded(fromisAttachmentNeeded).setMailId().doInitParamMap().makeStrictPrefix();
    }

    /**
     *
     * @param fromprefix Prefix for Action
     * @return Instance of Class
     * @throws Exception Throws exception when error occurs at RunTime
     */
    public static SprintsWebHook getInstanceForFetchStatus(final String fromprefix) throws Exception {
        //return new SprintsWebHook().setPrefix(fromprefix).setMailId().doInitCommonParamMap().makeProjectPrefix();
        return new SprintsWebHook().setPrefix(fromprefix).setMailId().makeProjectPrefix();
    }

    /**
     *
     * @param url Api to be invoked
     * @param method type of api call
     * @return Instance of Class
     */
    private RequestClient getClient(final String url, final String method) {
        RequestClient client = new RequestClient(url, method, paramMap);
        if (isAttachmentNeeded) {
            client.setAttachment(attachMap);
        }
        client.setOAuthHeader();
        return client;
    }

    /**
     *
     * @return Instance of Class
     */
    private SprintsWebHook makeStrictPrefix() {
        List<String> listOfPrefix =  Arrays.asList(prefix.split(",")).stream()
                .distinct()
                .collect(Collectors.toList());
        for (String itemprefix : listOfPrefix) {
            if (itemprefix.matches(Util.SPRINTSANDITEMREGEX)) {
                this.prefixList.add(itemprefix);
            }
        }
        this.prefixList = this.prefixList.stream().distinct().limit(10).collect(Collectors.toList());
        return this;
    }

    /**
     *
     * @return Instance of Class
     */
    private SprintsWebHook makeItemPrefix() {
        List<String> prefixList =  Arrays.asList(prefix.split(",")).stream()
                .distinct()
                .collect(Collectors.toList());
        for (String itemprefix : prefixList) {
            if (itemprefix.matches(Util.ITEM_REGEX)) {
                this.prefixList.add(itemprefix);
            }
        }
        this.prefixList = this.prefixList.stream().distinct().limit(10).collect(Collectors.toList());
        return this;
    }

    /**
     *
     * @return Instance of Class
     */
    private SprintsWebHook makeAddItemPrefix() {
        List<String> prefixList =  Arrays.asList(prefix.split(",")).stream()
                .distinct()
                .collect(Collectors.toList());
        for (String itemprefix : prefixList) {
            if (itemprefix.matches(Util.ADD_ITEM_REGEX)) {
                this.prefixList.add(itemprefix);
            }
        }
        this.prefixList = this.prefixList.stream().distinct().limit(10).collect(Collectors.toList());
        return this;
    }

    /**
     *
     * @return Instance of Class
     */
    private SprintsWebHook makeProjectPrefix() {
        List<String> listOfPrefix =  Arrays.asList(prefix.split(",")).stream()
                .distinct()
                .collect(Collectors.toList());
        for (String projectPrefix : listOfPrefix) {
            if(projectPrefix.matches(Util.PROJECT_REGEX)) {
                this.prefixList.add(projectPrefix);
            }
        }
        this.prefixList = this.prefixList.stream().distinct().limit(10).collect(Collectors.toList());
        return this;
    }

    /**
     *
     * @param pre List of prefix for Action
     * @return String format of Valid prefix
     */
    private String makeListAsString(List<String> pre) {
        return String.join(",", pre);
    }

    /**
     *
     * @return String format of response
     * @throws Exception Throws When an Runtime error Occurs
     */
    public String updateItemStatus() throws Exception {
        if (prefixList.isEmpty()) {
            throw new Exception("Invalid prefix given for Item Status update");
        }
        String[] statusArr = status.split(",");
        if(statusArr.length > 1){
            status = statusArr[0];
        }
        paramMap.put("action", ITEM_UPDATE_STATUS_ACTION);
        paramMap.put("status", status);
        paramMap.put("prefix", URLEncoder.encode(makeListAsString(prefixList), RequestClient.CHARSET));
        return getClient(config.getUpdateAction(), RequestClient.METHOD_POST).execute();
    }

    /**
     *
     * @return String format of response
     * @throws Exception Throws When an Runtime error Occurs
     */
    public String createItem() throws  Exception {
        if (prefixList.isEmpty()) {
            throw new Exception("Invalid prefix given for Create Item");
        }
       // paramMap.put("action", ITEM_CREATE);
        paramMap.put("prefix", prefixList.get(0));
        paramMap.put("description", getItemDesc());
        paramMap.put("issuename", getItemName());
        paramMap.put("issuetype", getItemType());
        if (assignee != null && !assignee.isEmpty()) {
            paramMap.put("assignee", assignee);
        }
        return getClient(config.getCreateIussue(), RequestClient.METHOD_POST).execute();
    }

    /**
     *
     * @return Boolean format of response
     * @throws Exception Throws When an Runtime error Occurs
     */
    public boolean addFeedStatus() throws Exception {

        if (prefixList.isEmpty()) {
            throw new Exception("Invalid project prefix");
        }
        paramMap.put("action", ADD_FEED_STATUS);
        paramMap.put("prefix", prefixList.get(0));
        paramMap.put("feedstatus", status);
        getClient(config.getPushFeedStatus(), RequestClient.METHOD_POST).execute();
        return true;
    }

    /**
     *
     * @return String format of response
     * @throws Exception Throws When an Runtime error Occurs
     */
    public String updateItempriority() throws Exception {
        if (prefixList.isEmpty()) {
            throw new Exception("Invalid prefix given for Item Priority update");
        }
        paramMap.put("action", ITEM_UPDATE_PRIORITY);
        paramMap.put("priority", priority);
        paramMap.put("prefix", URLEncoder.encode(makeListAsString(prefixList), RequestClient.CHARSET));
        return getClient(config.getUpdateAction(), RequestClient.METHOD_POST).execute();
    }

    /**
     *
     * @return List format of response
     * @throws Exception Throws When an Runtime error Occurs
     */
    public List<String> getStatusInPortal() throws Exception {
        if (prefix.isEmpty()) {
            return new ArrayList<>();
        }
        paramMap.put("action", GET_STATUS);
        paramMap.put("prefix", prefix);
        getClient(config.getStatusAction(), RequestClient.METHOD_GET).execute();
        return new ArrayList<>();
    }

    /**
     *
     * @return  Object format of response
     * @throws Exception Throws When an Runtime error Occurs
     */
    public Object addComment() throws Exception {
        if (prefixList.isEmpty()) {
            taskListener.error(sprintsLogparser("Empty prefix"));
            return null;
        }
        paramMap.put("action", ITEM_COMMENT_ACTION);
        paramMap.put("note", notes);
        paramMap.put("prefix", URLEncoder.encode(makeListAsString(prefixList), RequestClient.CHARSET));
       return getClient(config.getAddComment(), RequestClient.METHOD_POST).execute();
    }

    /**
     *
     * @return List format of response
     * @throws Exception Throws When an Runtime error Occurs
     */
    public List<String> fetchStatus() throws  Exception {
        isAuthedicated();
        paramMap.put("action", GET_STATUS);
        paramMap.put("prefix", prefix);
        return getListFromJSONArray(getClient(config.getStatusAction(), RequestClient.METHOD_GET).execute(), "projstatus");
    }

    /**
     *
     * @param resp Response JSON String
     * @param key key to fetch
     * @return  List format of response
     */
    private List<String> getListFromJSONArray(final String resp, final String key) {
        List<String> list = new ArrayList<>();
        if (resp != null && !resp.isEmpty()) {
            try {
                JSONObject json = new JSONObject(resp);
                if (json.getString("status").equals("success") && json.has(key)) {
                    JSONArray ja = json.getJSONArray(key);
                    for (int i = 0; i < ja.length(); i++) {
                        list.add(ja.getString(i));
                    }
                }
            } catch (JSONException e) {
                LOGGER.log(Level.WARNING, "", e);
            }
        }
        return list;
    }

    /**
     *
     * @throws Exception Throws when error occurs at Run time
     */
    private void isAuthedicated() throws Exception {
        if (!Util.isAuthendicated()) {
            throw new Exception("Sprints plugin is not Authenticated");
        }
    }
}
