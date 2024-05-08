package io.jenkins.plugins.model;

import static io.jenkins.plugins.Util.isEmpty;

import java.util.Objects;

public class ZSConnection {
    private String accountsDomain, serviceDomain, serviceAPIDomain;
    private String zoid, clientId, clientSecret, redirectURL, refreshToken;

    public void setAccountsDomain(String accountsDomain) {
        this.accountsDomain = Objects.requireNonNull(accountsDomain, "Accounts domain should not be null");
    }

    public void setServiceDomain(String serviceDomain) {
        this.serviceDomain = Objects.requireNonNull(serviceDomain, "Service domain should not be null");
    }

    public void setServiceAPIDomain(String serviceAPIDomain) {
        this.serviceAPIDomain = Objects.requireNonNull(serviceAPIDomain, "Service API domain should not be null");
    }

    public void setZoid(String zoid) {
        this.zoid = Objects.requireNonNull(zoid, "Zoid should not be null");
    }

    public void setClientId(String clientId) {
        this.clientId = Objects.requireNonNull(clientId, "Client id should not be null");
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = Objects.requireNonNull(clientSecret, "Client Secret should not be null");
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = Objects.requireNonNull(redirectURL, "Redirect URL should not be null");
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = Objects.requireNonNull(refreshToken, "Refresh token should not be null");
    }

    public ZSConnection() {
    }

    public String getAccountsDomain() {
        return accountsDomain;
    }

    public String getServiceDomain() {
        return serviceDomain;
    }

    public String getServiceAPIDomain() {
        return serviceAPIDomain;
    }

    public String getZoid() {
        return zoid;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Checking all the values are not null and empty string
     * 
     * @return boolean
     */
    public boolean isValid() {
        return !(isEmpty(accountsDomain) || isEmpty(serviceDomain) || isEmpty(serviceAPIDomain) || isEmpty(zoid)
                || isEmpty(clientId) || isEmpty(clientSecret) || isEmpty(redirectURL) || isEmpty(refreshToken));
    }
}
