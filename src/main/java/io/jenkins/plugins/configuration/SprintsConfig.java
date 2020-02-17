package io.jenkins.plugins.configuration;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
@Restricted(NoExternalUse.class)
public class SprintsConfig {
    private final String url, mailid, clientId, clientSecret, refrehToken;
    private String accessToken, redirectUrl;
    private Long accessTokenUpdatedTime = null;
    private static final String JOB_DELETE_URL = "/zsapi/jenkins/createjob/";
    private static final String CREATE_JOB = "/zsapi/jenkins/createjob/";
    private static final String CREATE_BUILD = "/zsapi/jenkins/createbuild/";
    private static final String BUILD_PUSH = "/zsapi/jenkins/buildpush/";
    private static final String PUSH_FEED_STATUS = "/zsapi/jenkins/feedstatus/";
    private static final String CREATE_ISSUE = "/zsapi/jenkins/createissue/";
    private static final String UPDATE_ACTION = "/zsapi/jenkins/update/";
    private static final String ADD_COMMENT = "/zsapi/jenkins/addcomment/";
    private static final String STATUS_ACTION = "/zsapi/jenkins/status/";

    /**
     *
     * @param fromurl - Sprints team url
     * @param frommailid - team mail id
     * @param redirectUrl - api token
     */
    @Restricted(NoExternalUse.class)
    public SprintsConfig(final String fromurl, final String frommailid, final String redirectUrl, final String clientid, final String clientSecret, final String refreshToken ) {
        this.url = fromurl;
        this.mailid = frommailid;
        this.redirectUrl = redirectUrl;
        this.clientId = clientid;
        this.clientSecret = clientSecret;
        this.refrehToken = refreshToken;
    }

    /**
     *
     * @return Team url
     */
    public String getUrl() {
        String apiUrl = "https://sprints.zoho.";
        if(this.url.contains("zoho.com.au")){
            apiUrl = apiUrl.concat("com.au");
        } else if(this.url.contains("zoho.com")){
            apiUrl = apiUrl.concat("com.au");
        } else if(this.url.contains("zoho.eu")){
            apiUrl = apiUrl.concat("com.au");
        } else if(this.url.contains("zoho.in")){
            apiUrl = apiUrl.concat("com.au");
        } else {
            apiUrl = this.url;
        }
        return apiUrl;
    }

    /**
     *
     * @return Admin mailid
     */
    public String getMailid() {
        return mailid;
    }

    /**
     *
     * @return apitoken
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }


    /**
     *
     * @return delete job api
     */
    public String getJobDeleteUrl() {
        return url + JOB_DELETE_URL;
    }

    /**
     *
     * @return create job api
     */
    public String getCreateJob() {
        return url + CREATE_JOB;
    }

    /**
     *
     * @return create build api
     */
    public String getCreateBuild() {
        return url + CREATE_BUILD;
    }

    /**
     *
     * @return build push api
     */
    public String getBuildPush() {
        return url + BUILD_PUSH;
    }

    /**
     *
     * @return feed push api
     */
    public String getPushFeedStatus() {
        return url + PUSH_FEED_STATUS;
    }

    /**
     *
     * @return create issue api
     */
    public String getCreateIussue() {
        return url + CREATE_ISSUE;
    }

    /**
     *
     * @return update priority api
     */
    public String getUpdateAction() {
        return url + UPDATE_ACTION;
    }

    /**
     *
     * @return add comment api
     */
    public String getAddComment() {
        return url + ADD_COMMENT;
    }

    /**
     *
     * @return update update status api
     */
    public String getStatusAction() {
        return url + STATUS_ACTION;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRefrehToken() {
        return refrehToken;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getAccessTokenUpdatedTime() {
        return accessTokenUpdatedTime;
    }

    public void setAccessTokenUpdatedTime(long accessTokenUpdatedTime) {
        this.accessTokenUpdatedTime = accessTokenUpdatedTime;
    }

    public  String
    getAccountsUrl(){
        String accountsUrl = null;
        if(this.url.contains("zoho.com.au")){
            accountsUrl = "https://accounts.zoho.com.au";
        } else if(this.url.contains("zoho.com")){
            accountsUrl = "https://accounts.zoho.com";
        } else if(this.url.contains("zoho.eu")){
            accountsUrl = "https://accounts.zoho.eu";
        } else if(this.url.contains("zoho.in")){
            accountsUrl = "https://accounts.zoho.in";
        } else {
            accountsUrl = "https://accounts.csez.zohocorpin.com";
        }
        return accountsUrl;
    }

}
