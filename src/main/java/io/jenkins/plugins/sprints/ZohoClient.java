package io.jenkins.plugins.sprints;

import static io.jenkins.plugins.Util.getZSConnection;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import io.jenkins.plugins.configuration.ZSConnectionConfiguration;
import io.jenkins.plugins.exception.ZSprintsException;

public class ZohoClient {
    private static final Logger logger = Logger.getLogger(ZohoClient.class.getName());
    private static final Pattern RELATIVE_URL_PATTERN = Pattern.compile("\\$(\\d{1,2})");
    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";
    private int statusCode;
    private Map<String, Object> queryParam = new HashMap<>();
    private Map<String, String> header = new HashMap<>();
    private String api;
    private String method;
    private boolean isJsonBodyresponse = false;

    private ZohoClient() {
    }

    public static ZohoClient getInstance() {
        return new ZohoClient();
    }

    public ZohoClient(String api, String method, String... relativeUrlParams) throws Exception {
        this.api = constructUri(api, relativeUrlParams);
        this.method = method;
        setDefaultHeader();
    }

    public void setDefaultHeader() {
        header.put("X-ZA-SOURCE", "eiULZMmzMCRXCgFljRnxrA==");
        header.put("Authorization", "Zoho-oauthtoken " + getZSConnection().getAccessToken());
    }

    public ZohoClient addParameter(String key, String value) {
        if (key != null && value != null && !value.trim().isEmpty()) {
            queryParam.put(key, value);
        }
        return this;
    }

    public ZohoClient setJsonBodyresponse(boolean isJsonBodyresponse) {
        this.isJsonBodyresponse = isJsonBodyresponse;
        return this;
    }

    public ZohoClient addParameter(String key, JSONArray value) {
        if (value != null && !value.isEmpty()) {
            queryParam.put(key, value);
        }
        return this;
    }

    public boolean isSuccessRequest() {
        return statusCode == HttpServletResponse.SC_OK || statusCode == HttpServletResponse.SC_CREATED;
    }

    private RequestClient getClient() throws Exception {
        return new RequestClient.RequestClientBuilder(api, method, queryParam)
                .setHeader(header)
                .setJsonBodyContent(isJsonBodyresponse)
                .build();
    }

    public String execute() throws Exception {
        logger.info(api);
        prependDomain();
        checkAndSetOAuthToken();
        RequestClient client = getClient();
        HttpResponse<String> response = client.execute();
        String responseString = response.body();
        logger.info(responseString);
        statusCode = response.statusCode();
        if (isOAuthExpired(responseString)) {
            generateNewAccessToken();
            response = getClient().execute();
            responseString = response.body();
            statusCode = response.statusCode();
        }
        logger.info(responseString);
        if (isSuccessRequest()) {
            return responseString;
        }
        throw new ZSprintsException(new JSONObject(responseString).toString());
    }

    private boolean isOAuthExpired(String response) {
        return statusCode == HttpServletResponse.SC_BAD_REQUEST &&
                new JSONObject(response).optInt("code", 0) == 7601;
    }

    private void checkAndSetOAuthToken() throws Exception {
        ZSConnectionConfiguration config = getZSConnection();
        if (config.getAccessToken() != null && config.getAccessToken().length() == 0) {
            generateNewAccessToken();
        }
        this.api = config.getZSApiPath() + api;
    }

    private void prependDomain() throws Exception {
        this.api = getZSConnection().getZSApiPath() + api;
    }

    public synchronized void generateNewAccessToken() throws Exception {
        ZSConnectionConfiguration config = getZSConnection();
        logger.info("New Token method called");
        String accessToken = null;
        HttpResponse<String> response = new RequestClient.RequestClientBuilder(
                config.getAccountsDomain() + "/oauth/v2/token", METHOD_POST)
                .setParameter("grant_type", "refresh_token")
                .setParameter("client_id", config.getClientId())
                .setParameter("client_secret", config.getClientSecret())
                .setParameter("refresh_token", config.getRefreshToken())
                .setParameter("redirect_uri", config.getRedirectURL())
                .build()
                .execute();

        if (response.statusCode() == HttpServletResponse.SC_OK) {
            JSONObject respObj = new JSONObject(response.body());
            if (respObj.has("access_token")) {
                logger.info("New Access token created ");
                accessToken = respObj.getString("access_token");
                config.setAccessToken(accessToken);
                config.save();
                header.put("Authorization", "Zoho-oauthtoken " + accessToken);
                logger.info("New Token generated");
            } else {
                logger.log(Level.INFO, "Error occurred during new access token creation Error - {0}", response.body());
            }
        }
    }

    private String constructUri(String url, String urlParams[]) throws Exception {
        if (urlParams == null) {
            return url;
        }
        StringBuffer urlBuilder = new StringBuffer();
        Matcher matcher = RELATIVE_URL_PATTERN.matcher(url);
        while (matcher.find()) {
            matcher.appendReplacement(urlBuilder,
                    URLEncoder.encode(urlParams[Integer.parseInt(matcher.group(1)) - 1],
                            StandardCharsets.UTF_8.name()));
        }
        matcher.appendTail(urlBuilder);
        return urlBuilder.toString();
    }
}
