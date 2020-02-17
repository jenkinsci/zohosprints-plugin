package io.jenkins.plugins.configuration;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import hudson.Util;
import hudson.util.Secret;

/**
 *@author selvavignesh.m
 * @version 1.0
 */
@NameWith(SprintsApiToken.NameProvider.class)
public interface SprintsApiToken extends StandardCredentials {
    Secret getClientId();
    Secret getClientSecret();
    Secret getRefreshToken();
    String getRedirectUrl();

    class NameProvider extends CredentialsNameProvider<SprintsApiToken> {
        /**
         *
         * @param c
         * @return String
         */
        @Override
        public String getName(SprintsApiToken c) {
            String description = Util.fixEmptyAndTrim(c.getDescription());
            return "Sprints API Token" + (description != null ? " (" + description + ")" : "");
        }
    }
}
