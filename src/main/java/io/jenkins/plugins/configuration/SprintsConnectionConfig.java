package io.jenkins.plugins.configuration;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import hudson.Extension;
import hudson.model.Item;
import hudson.scheduler.Hash;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import io.jenkins.plugins.Messages;
import io.jenkins.plugins.sprints.RequestClient;
import io.jenkins.plugins.sprintsdata.SprintsDataMigration;
import io.jenkins.plugins.util.OAuthUtil;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.interceptor.RequirePOST;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global Configuration for sprints
 *@author selvavignesh.m
 * @version 1.0
 */
@Extension
public class SprintsConnectionConfig extends GlobalConfiguration {
    private static final Logger LOGGER = Logger.getLogger(SprintsConnectionConfig.class.getName());
    private List<SprintsConnection> connections = new ArrayList<>();
    private transient Map<String, SprintsConnection> connectionMap = new HashMap<>();

    private  boolean isMigrated;
    private Secret zsheader;
    private String accountsUrl, domain;

    public String getAccountsUrl() {
        return accountsUrl;
    }

    public void setAccountsUrl(String accountsUrl) {
        this.accountsUrl = accountsUrl;
    }

    public String getDoamin() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    public String getZsheader() {
        return Secret.toString(zsheader);
    }

    public void setZsheader(String zsheader) {
        this.zsheader = Secret.fromString(zsheader);
    }
    /**
     * Load and refersh the connection Map
     */
    public SprintsConnectionConfig() {
        load();
        refreshConnectionMap();
    }

    /**
     *
     * @param req stapler request Object
     * @param json Contains value and key
     * @return true/false
     * @throws FormException if querying of form throws an error
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        connections = req.bindJSONToList(SprintsConnection.class, json.get("connections"));
        if (connections.isEmpty()) {
            setMigrated(false);
            setZsheader("");
        }
        refreshConnectionMap();
        save();
        return super.configure(req, json);
    }

    /**
     *
     * @param newConnections List of Sprints Connections
     */
    public void setConnections(final List<SprintsConnection> newConnections) {
        connections = new ArrayList<>();
        for (SprintsConnection connection : newConnections) {
            addConnection(connection);
        }
    }

    /**
     *
     * @return boolean
     */
    public boolean isMigrated() {
        return isMigrated;
    }

    /**
     *
     * @param migrated Migartoin status
     */
    public void setMigrated(final boolean migrated) {
        isMigrated = migrated;
    }


    /**
     *
     * @param connection Connection to be added
     */
    public void addConnection(SprintsConnection connection) {
        connections.add(connection);
        connectionMap.put(connection.getName(), connection);
    }

    /**
     *
     * @return List of Sprints Connection
     */
    public List<SprintsConnection> getConnections() {
        return connections;
    }

    /**
     *
     * @return SprintsConfig Curretn sprints connection
     */
    public SprintsConfig getClient() {
        for (Map.Entry<String, SprintsConnection> entry : connectionMap.entrySet()) {
            return entry.getValue().getClient();
        }
        return null;
    }

    /**
     * Refresh the Connection Maps
     * @version 1.0
     */
    private void refreshConnectionMap() {
        connectionMap.clear();
        for (SprintsConnection connection : connections) {
            connectionMap.put(connection.getName(), connection);
        }
    }

    /**
     *
     * @param id id
     * @param value Name of rhe Connection
     * @return FormValidation
     */
    public FormValidation doCheckName(@QueryParameter String id, @QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.error(Messages.config_url_required());
        } else {
            return FormValidation.ok();
        }
    }

    /**
     *
     * @param value Sprints Portal Url
     * @return FormValidation
     */
    public FormValidation doCheckUrl(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.error(Messages.config_url_required());
        } else {
            return FormValidation.ok();
        }
    }
    /**
     *
     * @param value Apitoken id from Credential Plugin
     * @return FormValidation
     */
    public FormValidation doCheckApiTokenId(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.error(Messages.config_apiToken_required());
        } else {
            return FormValidation.ok();
        }
    }

    /**
     *
     * @param apiTokenId Apitoken id from Credential plugin
     * @param url Sprints portal Url
     * @param mailid Admin maild id
     * @return FormValidation
     */
    @RequirePOST
    @Restricted(DoNotUse.class) // WebOnly
    public FormValidation doTestConnection(
                                           @QueryParameter String apiTokenId,
                                           @QueryParameter String url,
                                           @QueryParameter String mailid ) {
        Jenkins.getInstanceOrNull().checkPermission(Jenkins.ADMINISTER);
        try {
            SprintsConfig config = new SprintsConnection("", url, mailid, apiTokenId).getClient();
            String accessToken = OAuthUtil.getNewAccessToken(config);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Authorization", "Zoho-oauthtoken "+ accessToken);
            Map<String, Object> param = new HashMap<>();
            param.put("action", "doauthendiate");
            param.put("mailid",mailid);
            String portal = url + "/zsapi/jenkins/authendicate/";
            RequestClient request = new RequestClient(portal, RequestClient.METHOD_GET, param, headerMap);
            org.json.JSONObject reposne = new org.json.JSONObject(request.execute());

            if (reposne != null && reposne.getString("status").equalsIgnoreCase("success")) {
                if (reposne.has("isMigrated") && !reposne.getBoolean("isMigrated") && reposne.getBoolean("isZuidAvailable")) {
                    String header = reposne.getString("zsheader");
                    List<Item> itemList = Jenkins.getInstance().getAllItems();
                    new SprintsDataMigration(itemList, url, header, accessToken).run();
                    setZsheader(header);
                } else if (reposne.getBoolean("isZuidAvailable")) {
                    setZsheader(reposne.getString("zsheader"));
                }
                setAccountsUrl(reposne.optString("accountsurl"));
                setDomain(reposne.optString("domain"));
                setMigrated(true);
            } else {
                setMigrated(false);
                return FormValidation.ok(reposne.getString("message"));
            }

            return FormValidation.ok(Messages.config_connection_success());
        } catch (Exception e) {
            setMigrated(false);
            LOGGER.log(Level.WARNING,"",e);
            return FormValidation.error(Messages.config_connection_error(e.getMessage()));
        }
    }


    /**
     *
     * @param name Name of the Connection
     * @param url Url of the Sprints Portal
     * @return ListBoxModel
     */
    public ListBoxModel doFillApiTokenIdItems(@QueryParameter String name, @QueryParameter String url) {
        if (Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
            AbstractIdCredentialsListBoxModel<StandardListBoxModel, StandardCredentials> options = new StandardListBoxModel()
                    .includeEmptyValue()
                    .includeMatchingAs(ACL.SYSTEM,
                            Jenkins.getActiveInstance(),
                            StandardCredentials.class,
                            URIRequirementBuilder.fromUri(url).build(),
                            new SprintsCredentialMatcher());
            if (name != null && connectionMap.containsKey(name)) {
                String apiTokenId = connectionMap.get(name).getApiTokenId();
                options.includeCurrentValue(apiTokenId);
                for (ListBoxModel.Option option : options) {
                    if (option.value.equals(apiTokenId)) {
                        option.selected = true;
                    }
                }
            }
            return options;
        }
        return new StandardListBoxModel();
    }


    /**
     * @version 1.0
     */
    private static class SprintsCredentialMatcher implements CredentialsMatcher {
        /**
         *
         * @param credentials Credentaila Object
         * @return if given credential matches true else false
         */
        @Override
        public boolean matches(@Nonnull final Credentials credentials) {
            try {
                return credentials instanceof SprintsApiToken;
            } catch (Throwable e) {
                return false;
            }
        }
    }

}
