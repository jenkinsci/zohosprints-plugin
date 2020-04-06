package io.jenkins.plugins.util;

import io.jenkins.plugins.configuration.SprintsConfig;
import io.jenkins.plugins.configuration.SprintsConnectionConfig;
import io.jenkins.plugins.sprints.RequestClient;
import jenkins.model.Jenkins;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OAuthUtil {
    private static final Logger logger = Logger.getLogger(OAuthUtil.class.getName());
    public static synchronized String getNewAccessToken(SprintsConfig config) throws Exception {
        logger.info("New Access token method called");
        if(config.getAccessTokenUpdatedTime() != null && (System.currentTimeMillis() - config.getAccessTokenUpdatedTime()) <= TimeUnit.MILLISECONDS.convert(50, TimeUnit.MINUTES)){
            logger.info("Access token already available");
            return config.getAccessToken();
        }
        Map<String, Object> param = new HashMap<>();
        String accessToken = null;
        param.put("grant_type", "refresh_token");
        param.put("client_id", config.getClientId());
        param.put("client_secret", config.getClientSecret());
        param.put("refresh_token", config.getRefrehToken());
        param.put("redirect_uri", config.getRedirectUrl());
        RequestClient client = new RequestClient(config.getAccountsUrl() +"/oauth/v2/token", RequestClient.METHOD_POST, param);
        String resp = client.execute();
        if(resp != null && !resp.isEmpty() && resp.startsWith("{")) {
            JSONObject respObj = new JSONObject(resp);
            if(respObj.has("access_token")) {
                logger.info("New Access token created ");
                accessToken = respObj.getString("access_token");
                config.setAccessToken(accessToken);
                config.setAccessTokenUpdatedTime(System.currentTimeMillis());
            } else {

                logger.log(Level.INFO,"Error occurred during new access token creation Error - {0}", resp);
            }
        }
        return accessToken;
    }
    private static String getAccessToken() throws Exception {
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        SprintsConfig zspojo = conf.getClient();
        if( zspojo.getAccessTokenUpdatedTime() != null && (System.currentTimeMillis() - zspojo.getAccessTokenUpdatedTime()) <= TimeUnit.MILLISECONDS.convert(50, TimeUnit.MINUTES)) {
            return zspojo.getAccessToken();
        }
        return getNewAccessToken(zspojo);
    }
    public static Map<String, String> getOAuthHeader() {
        Map<String, String> headerMap = new HashMap<>();
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        SprintsConnectionConfig conf = extnList.get(0);
        try {
            headerMap.put("X-ZS-JENKINS-ID", conf.getZsheader());
            headerMap.put("Authorization", "Zoho-oauthtoken "+getAccessToken());

        } catch (Exception e){
            logger.log(Level.SEVERE, "error in accesstoken creation", e);
        }
        return headerMap;
    }
}
