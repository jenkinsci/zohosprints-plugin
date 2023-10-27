package io.jenkins.plugins.api;

import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import io.jenkins.plugins.Util;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.Sprint;
import io.jenkins.plugins.sprints.ZohoClient;

public class SprintAPI {
    private static final String CREATE_SPRINT_API = "/projects/no-$1/sprints/";
    private static final String UPDATE_SPRINTS_API = "/projects/no-$1/sprints/no-$2/";
    private static final String START_SPRINT_API = UPDATE_SPRINTS_API + "start/";
    private static final String COMPLETE_SPRINT_API = UPDATE_SPRINTS_API + "complete/";
    private static final String ADD_SPRINT_COMMENT_API = UPDATE_SPRINTS_API + "notes/";
    Function<String, String> paramValueReplacer;

    private SprintAPI(Function<String, String> paramValueReplacer) {
        this.paramValueReplacer = paramValueReplacer;
    }

    public static SprintAPI getInstance(Function<String, String> replacer) {
        return new SprintAPI(replacer);
    }

    private JSONArray getUsers(String mailIds, String projectNumber) throws Exception {
        if (mailIds != null && !mailIds.trim().isEmpty()) {
            return Util.getZSUserIds(paramValueReplacer, projectNumber, mailIds);
        }
        return new JSONArray();
    }

    public String create(Sprint sprint) throws Exception {

        ZohoClient.Builder client = new ZohoClient.Builder(CREATE_SPRINT_API, ZohoClient.METHOD_POST,
                paramValueReplacer, sprint.getProjectNumber());
        JSONArray scrumMasterUserIds = getUsers(sprint.getScrummaster(), sprint.getProjectNumber());
        if (!scrumMasterUserIds.isEmpty()) {
            client.addParameter("scrummaster", "" + scrumMasterUserIds.get(0));
        }
        JSONArray sprintUsers = getUsers(sprint.getUsers(), sprint.getProjectNumber());
        if (!sprintUsers.isEmpty()) {
            client.addParameter("users", sprintUsers);
        }
        return addorUpdate(client, sprint, "Sprint added successfully");
    }

    public String update(Sprint sprint) throws Exception {
        ZohoClient.Builder client = new ZohoClient.Builder(UPDATE_SPRINTS_API, ZohoClient.METHOD_POST,
                paramValueReplacer, sprint.getProjectNumber(),
                sprint.getSprintNumber());
        return addorUpdate(client, sprint, "Sprint updated successfully");
    }

    private String addorUpdate(ZohoClient.Builder client, Sprint sprint, String successMessage) throws Exception {
        client.addParameter("name", sprint.getName())
                .addParameter("description", sprint.getDescription())
                .addParameter("duration", sprint.getDuration())
                .addParameter("startdate", sprint.getStartdate())
                .addParameter("enddate", sprint.getEnddate());
        Util.setCustomFields(sprint.getCustomFields(), client);
        String response = client.build().execute();
        String message = new JSONObject(response).optString("message", null);
        if (message == null) {
            return successMessage;
        }
        throw new ZSprintsException(message);
    }

    public String start(Sprint sprint) throws Exception {
        new ZohoClient.Builder(START_SPRINT_API, ZohoClient.METHOD_POST, paramValueReplacer, sprint.getProjectNumber(),
                sprint.getSprintNumber())
                .build()
                .execute();
        return "Sprint has been started successfully";
    }

    public String complete(Sprint sprint) throws Exception {
        String response = new ZohoClient.Builder(COMPLETE_SPRINT_API, ZohoClient.METHOD_POST, paramValueReplacer,
                sprint.getProjectNumber(),
                sprint.getSprintNumber())
                .addParameter("action", "complete")
                .build()
                .execute();
        int inProgressItemCount = new JSONObject(response).optInt("completedDate", 0);
        if (inProgressItemCount == 0) {
            return "Sprint has been completed successfully";
        }
        throw new ZSprintsException("Unable to complete Sprint");

    }

    public String addComment(Sprint sprint) throws Exception {
        new ZohoClient.Builder(ADD_SPRINT_COMMENT_API, ZohoClient.METHOD_POST, paramValueReplacer,
                sprint.getProjectNumber(),
                sprint.getSprintNumber())
                .addParameter("name", sprint.getNote())
                .build()
                .execute();
        return "Sprint Comment added successfully";
    }
}
