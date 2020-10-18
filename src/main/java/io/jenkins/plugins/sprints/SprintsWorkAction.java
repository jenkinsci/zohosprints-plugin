package io.jenkins.plugins.sprints;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import io.jenkins.plugins.util.Util;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.jenkins.plugins.util.Util.expandContent;
import static io.jenkins.plugins.util.Util.getAdminMailid;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class SprintsWorkAction {

    private static final Logger LOGGER = Logger.getLogger(SprintsWorkAction.class.getName());
    public static final String POST_BUILD_TYPE = "POST_BUILD_TYPE";
    public static final String BUILD_TYPE = "BUILD_TYPE";

    private String itemName = null, prefix = null, itemDesc = null, itemType = null, itemAssignee = null, note = null, status = null, priority = null, buildActionType = null, mailid = null;
    private boolean isItemAddAttachment = false, isCommentAttachment = false, onFailure = false;
    private AbstractBuild<?, ?> build;
    private BuildListener listener;
   // private BuildJobAction job;
    private boolean isNewBuildJobAction;

    /**
     *
     * @return Build Action type
     */
    public String getBuildActionType() {
        return buildActionType;
    }

    /**
     *
     * @return mail id of the portal Admin
     */
    public String getMailid() {
        return mailid;
    }

    /**
     *
     * @return Instance of Class
     */
    public SprintsWorkAction setMailid() {
        this.mailid = getAdminMailid();
        return this;
    }

    /**
     *
     * @param fromBuildActionType Type of build {builder/post build}
     * @return Instance of Class
     */
    public SprintsWorkAction setBuildActionType(final String fromBuildActionType) {

        this.buildActionType = fromBuildActionType;
        return this;
    }

    /**
     *
     * @return Job Action of build for Sprints plugin
     */
//    public BuildJobAction getJob() {
//        return job;
//    }

    /**
     *
     */
//    public void setJob() {
//        List<Object> jobList = getBuildJobAction(build, listener);
//        this.job = (BuildJobAction) jobList.get(0);
//        this.isNewBuildJobAction = (boolean) jobList.get(1);
//    }

    /**
     *
     * @return  AbstractBuild Object of Build
     */
    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    /**
     *
     * @param fromBuild AbstractBuild Object of Build
     * @return Instance of Class
     */
    public SprintsWorkAction setBuild(final AbstractBuild<?, ?> fromBuild) {
        this.build = fromBuild;
        //setJob();
        return this;
    }

    /**
     *
     * @return listener Receives events that happen during a build
     */
    public BuildListener getListener() {
        return listener;
    }

    /**
     *
     * @param buildListener listener Receives events that happen during a build
     * @return Instance of Class
     */
    public SprintsWorkAction setListener(final BuildListener buildListener) {
        this.listener = buildListener;
        return this;
    }

    /**
     *
     * @return priority to be updated for Item
     */
    public String getPriority() {
        return priority;
    }

    /**
     *
     * @param fromPriority priority to update in Sprints Item
     * @return Instance of Class
     */
    public SprintsWorkAction setPriority(final String fromPriority) {
        this.priority = expandContent(getBuild(), getListener(), fromPriority);
        return this;
    }

    /**
     *
     * @return Comment to be added
     */
    public String getNote() {
        return note;
    }

    /**
     *
     * @param fromNote Comment to add in Sprints Item
     * @return Instance of Class
     */
    public SprintsWorkAction setNote(final String fromNote) {
        this.note = expandContent(getBuild(), getListener(), fromNote);
        return this;
    }

    /**
     *
     * @return Item Name
     */
    public String getItemName() {
        return itemName;
    }

    /**
     *
     * @param fromItemName Name of the Sprints Item
     * @return Instance of Class
     */
    public SprintsWorkAction setItemName(final String fromItemName) {
        this.itemName = expandContent(getBuild(), getListener(), fromItemName);
        return this;
    }

    /**
     *
     * @return prefix of the Item/project
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @param fromPrefix Prefix for the Action
     * @return Instance of Class
     */
    public SprintsWorkAction setPrefix(final String fromPrefix) {
        this.prefix = expandContent(getBuild(), getListener(), fromPrefix.toUpperCase());
        return this;
    }

    /**
     *
     * @return Description of the Item
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     *
     * @param fromItemDesc Sprints Item Description
     * @return Instance of Class
     */
    public SprintsWorkAction setItemDesc(final String fromItemDesc) {
        this.itemDesc = expandContent(getBuild(), getListener(), fromItemDesc);
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
     * @param fromItemType Type of Sprints Item
     * @return Instance of Class
     */
    public SprintsWorkAction setItemType(final String fromItemType) {
        this.itemType = expandContent(getBuild(), getListener(), fromItemType);
        return this;
    }

    /**
     *
     * @return Item Assignee
     */
    public String getItemAssignee() {
        return itemAssignee;
    }

    /**
     *
     * @param fromItemAssignee Assignee of Item
     * @return Instance of Class
     */
    public SprintsWorkAction setItemAssignee(final String fromItemAssignee) {
        this.itemAssignee = fromItemAssignee != null && !fromItemAssignee.isEmpty() ? expandContent(getBuild(), getListener(), fromItemAssignee) : null;
        return this;
    }

    /**
     *
     * @return does item need log file as attachemnt?
     */
    public boolean isItemAddAttachment() {
        return isItemAddAttachment;
    }

    /**
     *
     * @param itemAddAttachment whether need to add build log as Attachment with Sprints Item Creation
     * @return Instance of Class
     */
    public SprintsWorkAction setItemAddAttachment(final boolean itemAddAttachment) {
        isItemAddAttachment = itemAddAttachment;
        return this;
    }

    /**
     *
     * @return does comment api has attachemnt file to be added
     */
    public boolean isCommentAttachment() {
        return isCommentAttachment;
    }

    /**
     *
     * @param commentAttachment whether need to add build log along with Sprints Item Comment
     * @return Instance of Class
     */
    public SprintsWorkAction setCommentAttachment(final boolean commentAttachment) {
        isCommentAttachment = commentAttachment;
        return this;
    }

    /**
     *
     * @return status of the item / feed
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param fromStatus Status to update in Sprints Item/ Feed to  updated
     * @return Instance of Class
     */
    public SprintsWorkAction setStatus(final String fromStatus) {
        this.status = expandContent(build, getListener(), fromStatus);
        return this;
    }

    /**
     *
     * @param frombuild AbstractBuild Object of Build
     * @param fromlistener listener Receives events that happen during a build
     * @param frombuildType Type of build {builder/post build}
     * @param fromprefix For which sprint Item to create
     * @param fromitemName Name of the Sprints Item
     * @param fromitemDesc Sprints Item Description
     * @param fromitemType Sprints Item Type
     * @param fromitemAssignee Sprints Item Assignee
     * @param fromisItemAddAttachment Need to Add build log as a Attachment while create the Sprints Item
     * @return Instance of Class
     */
    public static SprintsWorkAction getInstanceForItemCreate(final AbstractBuild<?, ?> frombuild, final BuildListener fromlistener, final String frombuildType,
                                                             final String fromprefix, final String fromitemName, final String fromitemDesc, final String fromitemType,
                                                             final String fromitemAssignee, final boolean fromisItemAddAttachment) {
        return new SprintsWorkAction(frombuildType)
                .setBuild(frombuild)
                .setListener(fromlistener)
                .setPrefix(fromprefix)
                .setMailid()
                .setItemName(fromitemName)
                .setItemDesc(fromitemDesc)
                .setItemType(fromitemType)
                .setItemAssignee(fromitemAssignee)
                .setItemAddAttachment(fromisItemAddAttachment);
    }

    /**
     * call setPrefix method instead of assign
     * @param frombuild AbstractBuild Object of Build
     * @param fromlistener listener Receives events that happen during a build
     * @param frombuildType Type of build {builder/post build}
     * @param fromprefix prefix for which Sprint Item Status to Update
     * @param fromstatus Sprints Item Status to Update
     * @return Instance of Class
     */
    public static SprintsWorkAction getInstanceForStatusUpdate(final AbstractBuild<?, ?> frombuild, final BuildListener fromlistener, final String frombuildType,
                                                               final String fromprefix, final String fromstatus) {
        return new SprintsWorkAction(frombuildType)
                .setBuild(frombuild)
                .setListener(fromlistener)
                .setPrefix(fromprefix)
                .setMailid()
                .setStatus(fromstatus);
               // .setOnFailure(onFailure);
    }

    /**
     * call setPrefix method instead of assign
     * @param frombuild AbstractBuild Object of Build
     * @param fromlistener listener Receives events that happen during a build
     * @param frombuildType Type of build {builder/post build}
     * @param fromprefix prefix for in which Sprint Item Comment to Add
     * @param fromnote Comment to add
     * @param fromisCommentAttachment is Build log need to add along with comment
     * @return Instance of Class
     */
    public static SprintsWorkAction getInstanceForAddComment(final AbstractBuild<?, ?> frombuild, final BuildListener fromlistener, final String frombuildType,
                                                             final String fromprefix, final String fromnote, final boolean fromisCommentAttachment) {
        return new SprintsWorkAction(frombuildType)
                .setBuild(frombuild)
                .setListener(fromlistener)
                .setPrefix(fromprefix)
                .setMailid()
                .setNote(fromnote)
                .setCommentAttachment(fromisCommentAttachment);
                //.setOnFailure(onfailure);
    }

    /**
     * call setPrefix method instead of assign
     * @param frombuild AbstractBuild Object of Build
     * @param fromlistener listener Receives events that happen during a build
     * @param frombuildType Type of build {builder/post build}
     * @param fromprefix prefix for for which Sprint Item Priority to Update
     * @param frompriority priority to Update
     * @return Instance of Class
     */
    public static SprintsWorkAction getInstanceForUpdatePriority(final AbstractBuild<?, ?> frombuild, final BuildListener fromlistener, final String frombuildType,
                                                                 final String fromprefix, final String frompriority) {
        return new SprintsWorkAction(frombuildType)
                .setBuild(frombuild)
                .setListener(fromlistener)
                .setPrefix(fromprefix)
                .setMailid()
                .setPriority(frompriority);
    }

    /**
     * call setPrefix method instead of assign
     * @param frombuild AbstractBuild Object of Build
     * @param fromlistener listener Receives events that happen during a build
     * @param frombuildType Type of build {builder/post build}
     * @param fromprefix In which Sprints Project feed status to be pushed
     * @param fromstatus Feed Status to  push
     * @return Instance of Class
     */
    public static SprintsWorkAction getInstanceForFeedStatus(final AbstractBuild<?, ?> frombuild, final BuildListener fromlistener, final String frombuildType,
                                                             final String fromprefix, final String fromstatus) {
        return new SprintsWorkAction(frombuildType)
                .setBuild(frombuild)
                .setListener(fromlistener)
                .setPrefix(fromprefix)
                .setStatus(fromstatus)
                .setMailid();
    }


    /**
     *
     * @param frombuildActionType Type of build {builder/post build}
     */
    private SprintsWorkAction(final String frombuildActionType) {
        this.buildActionType = frombuildActionType;
    }

    /**
     *
     * @return boolean format of response
     */
    public boolean createItem() {
       // SprintActionInterface item = AddItem.getInstance();
        //item.setProjectGivenForCreateItem(prefix);
        try {
            if(!StringUtils.isEmpty(itemAssignee) && !itemAssignee.matches(Util.MAIL_REGEX)) {
                listener.getLogger().println(parseLogMessage("Assignee mail id is not valid", true));
                return false;
            }
            String response = SprintsWebHook.getInstanceForCreateItem(build, listener, prefix, itemName, itemDesc,
                                                    itemType, itemAssignee, isItemAddAttachment).createItem();
            Object respObject = Util.parseResponse(response, "status");
            if (respObject != null && respObject.toString().equals("success")) {
                listener.getLogger().println(parseLogMessage("Item created", false));
            } else {
                listener.getLogger().println(parseLogMessage("Item not created", true));
            }
            return true;
        } catch (Exception e) {
            listener.getLogger().println(parseLogMessage("error occured and issue not updated", true));
            LOGGER.log(Level.WARNING, "", e);
        }
        return false;

    }

    /**
     *
     * @return boolean format of response
     */
    public boolean updateStatus() {
        boolean isSuccess = false;
        //SprintActionInterface item = Status.getInstance(build);
        try {
            String itemPrefix = expandContent(build, getListener(), getPrefix()).toUpperCase();
            //String statusStr = expandContent(build, getListener(), getStatus());
           /* item.setPrefix(itemPrefix);
            item.setStatus(statusStr);*/

            String response = SprintsWebHook.getInstatnceForUpdateStatus(build, listener, prefix, status, false).updateItemStatus();
            Object value = Util.parseResponse(response, "STATUS_UPDATED_ITEM");
            JSONArray ja = value != null ? new JSONArray(value.toString()) : new JSONArray();
            if (ja.length() > 0) {
                isSuccess = true;
                listener.getLogger().println(parseLogMessage("Updated Status for following Items " + ja.toString(), false));
                //item.setStatusUpdateItem(Util.getStringFromJSONArray(ja));
            } else {
                listener.error(parseLogMessage("None of the Items Status are updated", false));
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }
       /* setListOfObject(item);
        configBuildAction();*/
        return isSuccess;
    }

    /**
     *
     * @return boolean format of response
     */
    public boolean addcomment() {
        PrintStream print = listener.getLogger();
        String resp = null;
        Object obj = null;
        boolean isSuccess = false;
        try {
            SprintsWebHook sprint = SprintsWebHook.getInstanceForAddComment(build, listener, prefix, note, isCommentAttachment());
            if (prefix.matches(Util.SPRINTSANDITEMREGEX)) {
                obj = sprint.addComment();
            }
            if (obj != null) {
                JSONObject respObj = new JSONObject(obj.toString());
                JSONArray ja = respObj.optJSONArray("COMMENT_ADDED_ITEM");
                if (respObj.has("status") && respObj.optString("status").equalsIgnoreCase("success")
                             && ja != null && ja.length() > 0) {
                    print.println(parseLogMessage("Comment updated for --> " + ja.toString(), false));
                } else {
                    listener.error(parseLogMessage("Comment not added", true));
                }
                isSuccess = true;
            }


        } catch (Exception e) {
            listener.error("Exception occured " + e.getMessage());
            LOGGER.log(Level.WARNING, "", e);
        }
        return isSuccess;
    }

    /**
     *
     * @return boolean format of response
     */
    public boolean updatePriority() {
        boolean isSuccess = false;
      /*  SprintActionInterface item = Priority.getInstance();
        item.setPrefix(prefix);
        item.setPriority(priority);
*/
        try {

            String rslt = SprintsWebHook.getInstatnceForUpdatePriority(build, listener, prefix, priority).updateItempriority();
            Object respObj = Util.parseResponse(rslt, "UPDATED_ITEM_PRIORITY");
            String respString = respObj != null ? (String) respObj : null;
            if (respString != null && !respString.isEmpty()) {
                isSuccess = true;
                listener.getLogger().println(parseLogMessage("Updated Priority for following Items " + respString, false));
                //item.setPriorityUpdateItem(respString.substring(1, respString.length() - 1));
            } else {
                listener.error(parseLogMessage("None of the Item priority updated", true));
            }


        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "", e);
        }
       /* setListOfObject(item);
        configBuildAction();*/
        return isSuccess;
    }

    /**
     *
     * @return boolean format of response
     */
    public boolean addFeedStatus() {
        boolean isSuccess = false;
       /* SprintActionInterface item = FeedStatus.getInstance();
        item.setPrefix(prefix);
        item.setStatus(status);*/
        try {
            isSuccess = SprintsWebHook.getInstatnceForUpdateStatus(build, listener, prefix, status, true).addFeedStatus();
            if (isSuccess) {
               // item.setCreationStatus("Feed Status added");
                listener.getLogger().println(parseLogMessage("Feed Status added", false));
            } else {
                //item.setCreationStatus("Feed Status not added");
                listener.error(parseLogMessage("Feed Status not added", true));
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "addFeedStatus", e);
        }
        //setListOfObject(item);
        //configBuildAction();
        return isSuccess;
    }


   /* private void configBuildAction() {
        if (isNewBuildJobAction) {
            build.addAction(job);
        }
    }*/

    /**
     *
     * @param obj List of Action performed related to Sprints plugin
     */
    /*private void setListOfObject(SprintActionInterface obj) {
        if (buildActionType.equals(BUILD_TYPE)) {
            job.setPreList(obj);
        } else {
            job.setPostList(obj);
        }
    }*/

    /**
     *
     * @param message message to be parsed for Sprints plugin
     * @return  Log message prepend with ZohoSprints
     */
    private String parseLogMessage(final String message, final boolean isError) {
        return Util.sprintsLogparser(message, isError);
    }
}
