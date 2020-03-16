package io.jenkins.plugins.configuration;

import hudson.ExtensionList;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import java.util.ArrayList;
import java.util.List;

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
    private static final String DELETE_BUILD = "/zsapi/jenkins/deletebuild/";
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
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        if (extnList.isEmpty()) {
            return null;

        }
        SprintsConnectionConfig conf = extnList.get(0);
        return conf.getDoamin();
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
        return getUrl() + JOB_DELETE_URL;
    }

    /**
     *
     * @return create job api
     */
    public String getCreateJob() {
        return getUrl() + CREATE_JOB;
    }

    /**
     *
     * @return create build api
     */
    public String getCreateBuild() {
        return getUrl() + CREATE_BUILD;
    }
    public String getDeleteBuild() {
        return getUrl() +  DELETE_BUILD;
    }

    /**
     *
     * @return build push api
     */
    public String getBuildPush() {
        return getUrl() + BUILD_PUSH;
    }

    /**
     *
     * @return feed push api
     */
    public String getPushFeedStatus() {
        return getUrl() + PUSH_FEED_STATUS;
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
        return getUrl() + UPDATE_ACTION;
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
        return getUrl() + STATUS_ACTION;
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

    public  String getAccountsUrl(){
        List<SprintsConnectionConfig> extnList =  new ArrayList<>(Jenkins.getInstance().getExtensionList(SprintsConnectionConfig.class));
        if (extnList.isEmpty()) {
            return null;

        }
        SprintsConnectionConfig conf = extnList.get(0);
        String accUrl =  conf.getAccountsUrl();
        if(StringUtils.isEmpty(accUrl)){
           String[] split = this.url.split("\\.");
           int domainLength = split.length;
           StringBuilder domainUrl = new StringBuilder("https://accounts.");
            if(!this.url.contains("zoho.com") || !this.url.contains("zoho.in") || !this.url.contains("zoho.eu")) {
                domainUrl.append(split[domainLength-3]).append(".");
            }
           domainUrl.append(split[domainLength -2]);
            domainUrl.append(".");
            String domainAppender = split[domainLength-1].contains(":") ? split[domainLength-1].split(":")[0] : split[domainLength-1];
           if(domainAppender.contains("/")) {
               domainUrl.append(domainAppender.substring(0, domainAppender.length()-1));
           } else {
               domainUrl.append(domainAppender.substring(0, domainAppender.length()));
           }
           return domainUrl.toString();
        }
        return accUrl;
    }

}
