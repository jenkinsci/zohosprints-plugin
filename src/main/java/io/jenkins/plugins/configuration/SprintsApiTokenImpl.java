package io.jenkins.plugins.configuration;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import hudson.Extension;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SprintsApiTokenImpl extends BaseStandardCredentials implements SprintsApiToken {
    private Secret code, refreshToken, clientId, clientSecret;
    private String redirectUrl;
   private static final Logger LOGGER = Logger.getLogger(SprintsApiTokenImpl.class.getName());

    @DataBoundConstructor
    public SprintsApiTokenImpl(CredentialsScope scope, String id, String description, Secret clientId, Secret clientSecret, Secret refreshToken, String redirectUrl) {
        super(scope, id, description);
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
    }

    @Override
    public Secret getClientId() {
        return clientId;
    }

    @Override
    public Secret getClientSecret() {
        return clientSecret;
    }

    @Override
    public Secret getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return "Sprints API Token";
        }
    }
}
