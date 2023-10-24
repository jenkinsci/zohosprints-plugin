package io.jenkins.plugins.api;

import org.json.JSONArray;
import org.json.JSONObject;

import io.jenkins.plugins.Util;
import io.jenkins.plugins.exception.ZSprintsException;
import io.jenkins.plugins.model.Release;
import io.jenkins.plugins.sprints.ZohoClient;

public final class ReleaseAPI {
    private static final String CREATE_RELEASE_API = "/projects/no-$1/release/";
    private static final String UPDATE_RELEASE_API = "/projects/no-$1/release/no-$2/update/";
    private static final String ADD_COMMENT_API = "/projects/no-$1/release/no-$2/notes/";

    private ReleaseAPI() {

    }

    public static ReleaseAPI getInstance() {
        return new ReleaseAPI();
    }

    public String create(Release release) throws Exception {
        JSONArray ownerIds = null;
        String assignee = release.getOwners();
        if (assignee != null && !assignee.trim().isEmpty()) {
            ownerIds = Util.getZSUserIds(release.getProjectNumber(), assignee);
        }
        ZohoClient client = new ZohoClient(CREATE_RELEASE_API, ZohoClient.METHOD_POST, release.getProjectNumber())
                .setJsonBodyresponse(true)
                .addParameter("ownerIds", ownerIds);
        Util.setCustomFields(release.getCustomFields(), client);
        String response = addOrUpdateRelease(release, client);
        String message = new JSONObject(response).optString("message", null);
        if (message == null) {
            return "Release has been added";
        }
        throw new ZSprintsException(message);
    }

    public String update(Release release) throws Exception {
        ZohoClient client = new ZohoClient(UPDATE_RELEASE_API, ZohoClient.METHOD_POST, release.getProjectNumber(),
                release.getReleaseNumber())
                .setJsonBodyresponse(true);

        String response = addOrUpdateRelease(release, client);
        String message = new JSONObject(response).optString("i18nMessage", null);
        if (message == null) {
            return "Release has been updated";
        }
        throw new ZSprintsException(message);
    }

    private String addOrUpdateRelease(Release release, ZohoClient client) throws Exception {
        client.setJsonBodyresponse(true)
                .addParameter("name", release.getName())
                .addParameter("startdate", release.getStartdate())
                .addParameter("enddate", release.getEnddate())
                .addParameter("statusName", release.getStage())
                .addParameter("goal", release.getGoal());
        Util.setCustomFields(release.getCustomFields(), client);
        return client.execute();
    }

    public String addComment(Release release) throws Exception {

        new ZohoClient(ADD_COMMENT_API, ZohoClient.METHOD_POST, release.getProjectNumber(),
                release.getReleaseNumber())
                .addParameter("name", release.getNote())
                .execute();
        return "Release Comment added successfully";
    }
}
