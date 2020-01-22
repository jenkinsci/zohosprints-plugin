package io.jenkins.plugins.configuration;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.domains.Domain;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SprintsConnectionConfigTest {
    private static final String API_TOKEN = "secret";
    private static final String API_TOKEN_ID = "apiTokenId";
    @Rule
    public MockServerRule mockServer = new MockServerRule(this);
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();
    private MockServerClient mockServerClient;
    private String sprintsUrl;

    @Before
    public void setup() throws IOException {
        sprintsUrl = "http://localhost";
        for(CredentialsStore credentialsStore : CredentialsProvider.lookupStores(Jenkins.getInstance())) {
            if(credentialsStore instanceof SystemCredentialsProvider.StoreImpl) {
                List<Domain> domains = credentialsStore.getDomains();
                credentialsStore.addCredentials(domains.get(0), new StringCredentialsImpl(CredentialsScope.SYSTEM,API_TOKEN_ID,"Sprints API Token", Secret.fromString(API_TOKEN)));
            }
        }
    }

    @Test
    public void setConnectionTest() {
        SprintsConnection connection = new SprintsConnection("test","http://localhost","test@test.com",API_TOKEN_ID);
        List<SprintsConnection> connectionList = new ArrayList<>();
        connectionList.add(connection);
        SprintsConnectionConfig connectionConfig = jenkins.get(SprintsConnectionConfig.class);
        connectionConfig.setConnections(connectionList);
        assertThat(connectionConfig.getConnections(), is(connectionList));
    }

}
