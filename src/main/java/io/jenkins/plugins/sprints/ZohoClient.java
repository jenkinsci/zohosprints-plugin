package io.jenkins.plugins.sprints;

import static io.jenkins.plugins.Util.getZSConnection;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
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
    private boolean isJsonBodyresponse;
    final ZSConnectionConfiguration config = getZSConnection();
    Function<String, String> replaceparamValue;

    private ZohoClient() {
    }

    public static ZohoClient getInstance() {
        return new ZohoClient();
    }

    public ZohoClient(Builder builder) throws Exception {
        this.api = builder.url;
        this.isJsonBodyresponse = builder.isJsonBodyresponse;
        this.method = builder.method;
        this.header = builder.header;
        this.queryParam = builder.queryParam;
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

    private boolean isOAuthExpired(String response) {
        int code = 0;
        try {
            code = new JSONObject(response).optInt("code", 0);
        } catch (JSONException e) {
            throw new ZSprintsException(response);
        }
        return statusCode == HttpServletResponse.SC_BAD_REQUEST && (code == 7601 || code == 7700);
    }

    private String getResponseAsString(HttpResponse<String> response) {
        return response.body();
    }

    public String execute() throws Exception {
        logger.info(api);
        RequestClient client = getClient();
        HttpResponse<String> response = client.execute();
        String responseString = getResponseAsString(response);
        statusCode = response.statusCode();
        if (isOAuthExpired(responseString)) {
            logger.info("Retrying the request...");
            generateNewAccessToken();
            response = getClient().execute();
            responseString = getResponseAsString(response);
            statusCode = response.statusCode();
        }
        if (isSuccessRequest()) {
            return responseString;
        }
        throw new ZSprintsException(new JSONObject(responseString).toString());
    }

    public void generateNewAccessToken() throws Exception {
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

    public static class Builder {
        String url, method;
        Function<String, String> replacer;
        String[] relativeUrlParams;
        private Map<String, Object> queryParam = new HashMap<>();
        private Map<String, String> header = new HashMap<>();
        boolean isJsonBodyresponse = false;

        public Builder(String url, String method, Function<String, String> replacer,
                String... relativeUrlParams) throws Exception {
            this.url = url;
            this.method = method;
            this.replacer = replacer;
            this.relativeUrlParams = relativeUrlParams;
        }

        public Builder setJsonBodyresponse(boolean isJsonBodyresponse) {
            this.isJsonBodyresponse = isJsonBodyresponse;
            return this;
        }

        private void setDefaultHeader() {
            header.put("X-ZA-SOURCE", "eiULZMmzMCRXCgFljRnxrA==");
            header.put("Authorization", "Zoho-oauthtoken " + getZSConnection().getAccessToken());
        }

        private void constructURI() throws Exception {
            StringBuffer urlBuilder = new StringBuffer();
            Matcher matcher = RELATIVE_URL_PATTERN.matcher(url);
            while (matcher.find()) {
                matcher.appendReplacement(urlBuilder,
                        URLEncoder.encode(replacer.apply(relativeUrlParams[Integer.parseInt(matcher.group(1)) - 1]),
                                StandardCharsets.UTF_8.name()));
            }
            matcher.appendTail(urlBuilder);
            url = urlBuilder.toString();
        }

        public Builder addParameter(String key, JSONArray value) {
            if (value != null && !value.isEmpty()) {
                queryParam.put(key, value);
            }
            return this;
        }

        public Builder addParameter(String key, String value) {
            if (key != null && value != null && !value.trim().isEmpty()) {
                queryParam.put(key, replacer.apply(value));
            }
            return this;
        }

        private void prependDomain() throws Exception {
            url = getZSConnection().getZSApiPath() + url;
        }

        public ZohoClient build() throws Exception {
            constructURI();
            prependDomain();
            setDefaultHeader();
            return new ZohoClient(this);
        }
    }
}
