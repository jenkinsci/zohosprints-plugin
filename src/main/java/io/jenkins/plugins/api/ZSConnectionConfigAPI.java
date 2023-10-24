package io.jenkins.plugins.api;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.json.JsonBody;
import org.kohsuke.stapler.json.JsonHttpResponse;
import org.kohsuke.stapler.verb.POST;

import hudson.Extension;
import hudson.model.RootAction;
import io.jenkins.plugins.configuration.ZSConnectionConfiguration;
import io.jenkins.plugins.model.ZSConnection;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

@Extension
public class ZSConnectionConfigAPI implements RootAction {

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Sprints";
    }

    @Override
    public String getUrlName() {
        return "zohosprints";
    }

    @POST
    @WebMethod(name = "settings")
    public JsonHttpResponse doCreate(@JsonBody ZSConnection configuration) {
        Jenkins.get().getACL().checkPermission(Jenkins.ADMINISTER);
        JSONObject response = new JSONObject();
        int statusCode = HttpServletResponse.SC_OK;
        if (!configuration.isValid()) {
            response.put("status", "failed");
            response.put("message", "Mandatory filed(s) are missiong");
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } else {
            ZSConnectionConfiguration config = new ZSConnectionConfiguration(configuration);
            config.save();
        }
        return new JsonHttpResponse(response, statusCode);
    }

    @POST
    @WebMethod(name = "reset")
    public JsonHttpResponse doReset() {
        Jenkins.get().getACL().checkPermission(Jenkins.ADMINISTER);
        JSONObject response = new JSONObject();
        new ZSConnectionConfiguration(new ZSConnection()).save();
        response.put("status", "success");
        return new JsonHttpResponse(response, HttpServletResponse.SC_OK);
    }
}
