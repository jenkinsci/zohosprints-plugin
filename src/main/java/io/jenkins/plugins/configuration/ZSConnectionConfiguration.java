package io.jenkins.plugins.configuration;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import hudson.Extension;
import hudson.util.Secret;
import io.jenkins.plugins.model.ZSConnection;
import jenkins.model.GlobalConfiguration;

@Restricted(NoExternalUse.class)
@Extension
public class ZSConnectionConfiguration extends GlobalConfiguration {
    private String accountsDomain, serviceDomain, serviceAPIDomain, redirectURL;
    private Secret clientId, clientSecret, refreshToken, accessToken, zoid;

    public ZSConnectionConfiguration() {
        load();
    }

    public ZSConnectionConfiguration(ZSConnection connection) {
        withAccountsDomain(connection.getAccountsDomain())
                .withServiceDomain(connection.getServiceDomain())
                .withServiceAPIDomain(connection.getServiceAPIDomain())
                .withRedirectURL(connection.getRedirectURL())
                .withZoid(connection.getZoid())
                .withClientId(connection.getClientId())
                .withClientSecret(connection.getClientSecret())
                .withRefreshToken(connection.getRefreshToken());
    }

    private Secret getSecret(String value) {
        return Secret.fromString(value);
    }

    private String getString(Secret value) {
        return Secret.toString(value);
    }

    public String getAccountsDomain() {
        return accountsDomain;
    }

    private ZSConnectionConfiguration withAccountsDomain(String value) {
        this.accountsDomain = value;
        return this;
    }

    public String getServiceDomain() {
        return serviceDomain;
    }

    private ZSConnectionConfiguration withServiceDomain(String serviceDomain) {
        this.serviceDomain = serviceDomain;
        return this;
    }

    public String getServiceAPIDomain() {
        return serviceAPIDomain;
    }

    private ZSConnectionConfiguration withServiceAPIDomain(String serviceAPIDomain) {
        this.serviceAPIDomain = serviceAPIDomain;
        return this;
    }

    public String getZoid() {
        return getString(zoid);
    }

    private ZSConnectionConfiguration withZoid(String value) {
        this.zoid = getSecret(value);
        return this;
    }

    public String getClientId() {
        return getString(clientId);
    }

    private ZSConnectionConfiguration withClientId(String value) {
        this.clientId = getSecret(value);
        return this;
    }

    public String getClientSecret() {
        return getString(clientSecret);
    }

    private ZSConnectionConfiguration withClientSecret(String value) {
        this.clientSecret = getSecret(value);
        return this;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    private ZSConnectionConfiguration withRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
        return this;
    }

    public String getRefreshToken() {
        return getString(refreshToken);
    }

    private ZSConnectionConfiguration withRefreshToken(String value) {
        this.refreshToken = getSecret(value);
        return this;
    }

    public String getAccessToken() {
        return getString(accessToken);
    }

    public void setAccessToken(String value) {
        this.accessToken = getSecret(value);
    }

    public String getZSApiPath() {
        return String.format("%s/zsapi/team/%s", getServiceAPIDomain(), getZoid());
    }
}
