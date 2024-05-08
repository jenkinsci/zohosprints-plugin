package io.jenkins.plugins.api;

import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import io.jenkins.plugins.Util;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.Item;
import io.jenkins.plugins.sprints.ZohoClient;

public class WorkItemAPI {
    private static final String ADD_ITEM_COMMENT_API = "/projects/no-$1/sprints/no-$2/item/no-$3/notes/";
    private static final String ADD_ITEM_API = "/projects/no-$1/sprints/no-$2/item/";
    private static final String UPDATE_ITEM_API = "/projects/no-$1/sprints/no-$2/item/no-$3/";
    Function<String, String> paramValueReplacer;

    private WorkItemAPI(Function<String, String> paramValueReplacer) {
        this.paramValueReplacer = paramValueReplacer;
    }

    public static WorkItemAPI getInstance(Function<String, String> replacer) {
        return new WorkItemAPI(replacer);
    }

    public String addComment(Item item) throws Exception {
        new ZohoClient.Builder(ADD_ITEM_COMMENT_API, ZohoClient.METHOD_POST, paramValueReplacer,
                item.getProjectNumber(),
                item.getSprintNumber(),
                item.getItemNumber())
                .addParameter("name", item.getNote())
                .build()
                .execute();
        return "Work item comment added. Yay!";
    }

    public String addItem(Item item) throws Exception {
        JSONArray assigneeIds = null;
        String assignee = item.getAssignee();
        assigneeIds = Util.getZSUserIds(paramValueReplacer, item.getProjectNumber(), assignee);
        ZohoClient.Builder client = new ZohoClient.Builder(ADD_ITEM_API, ZohoClient.METHOD_POST, paramValueReplacer,
                item.getProjectNumber(),
                item.getSprintNumber())
                .addParameter("action", "additem")
                .addParameter("assignee", assigneeIds);
        return addOrupdateItem(item, client, "Work item created. Yay!");
    }

    public String updateItem(Item item) throws Exception {
        ZohoClient.Builder client = new ZohoClient.Builder(UPDATE_ITEM_API, ZohoClient.METHOD_POST, paramValueReplacer,
                item.getProjectNumber(),
                item.getSprintNumber(),
                item.getItemNumber())
                .addParameter("action", "updateitem");
        return addOrupdateItem(item, client, "Work item fields updated. Yay!");
    }

    private String addOrupdateItem(Item item, ZohoClient.Builder client, String successMessage) throws Exception {
        client.addParameter("name", item.getName())
                .addParameter("projitemtypename", item.getType())
                .addParameter("duration", item.getDuration())
                .addParameter("description", item.getDescription())
                .addParameter("startdate", item.getStartdate())
                .addParameter("enddate", item.getEnddate())
                .addParameter("priorityname", item.getPriority())
                .addParameter("statusname", item.getStatus());
        Util.setCustomFields(item.getCustomFields(), client);
        String response = client.build().execute();
        String message = new JSONObject(response).optString("message", null);
        if (message == null) {
            return successMessage;
        }
        throw new ZSprintsException(message);
    }
}
