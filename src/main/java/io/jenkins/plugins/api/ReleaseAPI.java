package io.jenkins.plugins.api;

import java.util.function.Function;

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
    Function<String, String> paramValueReplacer;

    private ReleaseAPI(Function<String, String> paramValueReplacer) {
        this.paramValueReplacer = paramValueReplacer;
    }

    public static ReleaseAPI getInstance(Function<String, String> replacer) {
        return new ReleaseAPI(replacer);
    }

    public String create(Release release) throws Exception {
        JSONArray ownerIds = null;
        String assignee = release.getOwners();
        if (assignee != null && !assignee.trim().isEmpty()) {
            ownerIds = Util.getZSUserIds(paramValueReplacer, release.getProjectNumber(), assignee);
        }
        ZohoClient.Builder clientBuilder = new ZohoClient.Builder(CREATE_RELEASE_API, ZohoClient.METHOD_POST,
                paramValueReplacer,
                release.getProjectNumber())
                .setJsonBodyresponse(true)
                .addParameter("ownerIds", ownerIds);
        String response = addOrUpdateRelease(release, clientBuilder);
        String message = new JSONObject(response).optString("message", null);
        if (message == null) {
            return "Release has been added";
        }
        throw new ZSprintsException(message);
    }

    public String update(Release release) throws Exception {
        ZohoClient.Builder client = new ZohoClient.Builder(UPDATE_RELEASE_API, ZohoClient.METHOD_POST,
                paramValueReplacer,
                release.getProjectNumber(),
                release.getReleaseNumber())
                .setJsonBodyresponse(true);

        String response = addOrUpdateRelease(release, client);
        String message = new JSONObject(response).optString("i18nMessage", null);
        if (message == null) {
            return "Release has been updated";
        }
        throw new ZSprintsException(message);
    }

    private String addOrUpdateRelease(Release release, ZohoClient.Builder clientBuilder) throws Exception {
        clientBuilder.setJsonBodyresponse(true)
                .addParameter("name", release.getName())
                .addParameter("startdate", release.getStartdate())
                .addParameter("enddate", release.getEnddate())
                .addParameter("statusName", release.getStage())
                .addParameter("goal", release.getGoal());
        Util.setCustomFields(release.getCustomFields(), clientBuilder);
        return clientBuilder.build().execute();
    }

    public String addComment(Release release) throws Exception {
        new ZohoClient.Builder(ADD_COMMENT_API, ZohoClient.METHOD_POST, paramValueReplacer, release.getProjectNumber(),
                release.getReleaseNumber())
                .addParameter("name", release.getNote())
                .build()
                .execute();
        return "Release Comment added successfully";
    }
}
