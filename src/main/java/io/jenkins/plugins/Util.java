package io.jenkins.plugins;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import hudson.util.FormValidation;
import io.jenkins.plugins.configuration.ZSConnectionConfiguration;
import io.jenkins.plugins.sprints.ZohoClient;
import jenkins.model.Jenkins;

public class Util {
    private static final String GET_PROJECT_USER_API = "/projects/no-$1/user/details/";

    public static JSONArray getZSUserIds(Function<String, String> paramValueReplacer, String projectNumber,
            String mailIds)
            throws Exception {
        if (mailIds == null || mailIds.trim().isEmpty()) {
            return new JSONArray();
        }
        String response = new ZohoClient.Builder(GET_PROJECT_USER_API, ZohoClient.METHOD_GET, paramValueReplacer,
                projectNumber)
                .addParameter("action", "projectusers")
                .addParameter("emailids", new JSONArray(paramValueReplacer.apply(mailIds).split(",")))
                .build()
                .execute();

        JSONObject userObj = new JSONObject(response).getJSONObject("userObj");
        Iterator<String> keys = userObj.keys();
        Object[] users = new Object[userObj.length()];
        int counter = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            users[counter++] = userObj.getJSONObject(key).get("zsuserId");
        }
        return new JSONArray(users);
    }

    public static ZSConnectionConfiguration getZSConnection() {
        List<ZSConnectionConfiguration> extnList = Jenkins.get().getExtensionList(ZSConnectionConfiguration.class);
        ZSConnectionConfiguration conf = extnList.get(0);
        conf.load();
        return conf;
    }

    public static void setCustomFields(String customFields, ZohoClient.Builder builder) {
        if (isEmpty(customFields)) {
            return;
        }
        String[] fields = customFields.split("\n");
        for (String field : fields) {
            String[] fieldArr = field.split("=");
            String key = fieldArr[0];
            String value = fieldArr[1];
            builder.addParameter(key, value);
        }

    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static FormValidation validateRequired(String value) {
        return FormValidation.validateRequired(value);
    }
}
