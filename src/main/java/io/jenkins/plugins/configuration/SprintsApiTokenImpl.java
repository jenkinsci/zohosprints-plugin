package io.jenkins.plugins.configuration;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import hudson.Extension;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SprintsApiTokenImpl extends BaseStandardCredentials implements SprintsApiToken {
    private Secret apiToken;
   private static final Logger LOGGER = Logger.getLogger(SprintsApiTokenImpl.class.getName());

    @DataBoundConstructor
    public SprintsApiTokenImpl(CredentialsScope scope, String id, String description, Secret apiToken) {
        super(scope, id, description);
        this.apiToken = apiToken;
    }

    @Override
    public Secret getApiToken() {
        return apiToken;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return "Sprints API Token";
        }
    }
}
