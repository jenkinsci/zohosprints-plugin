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
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import io.jenkins.plugins.configuration.ZSConnectionConfiguration;
import io.jenkins.plugins.exception.ZSprintsException;

@Restricted(NoExternalUse.class)
public class ZohoClient {
    private static final Logger logger = Logger.getLogger(ZohoClient.class.getName());
    private static final Pattern RELATIVE_URL_PATTERN = Pattern.compile("\\$(\\d{1,2})");
    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private int statusCode;
    private boolean isRetry = false;
    private RequestClient.RequestClientBuilder clientBuilder;
    final ZSConnectionConfiguration config = getZSConnection();
    Function<String, String> replaceparamValue;

    private ZohoClient() {
    }

    private ZohoClient(RequestClient.RequestClientBuilder clientBuilder) throws Exception {
        this.clientBuilder = clientBuilder;
    }

    private boolean isSuccessRequest(String responseString) {
        return (statusCode == HttpServletResponse.SC_OK
                || statusCode == HttpServletResponse.SC_CREATED)
                && !new JSONObject(responseString).has("code");
    }

    private boolean isTokenExpired(String response) {
        int code = 0;
        try {
            code = new JSONObject(response).optInt("code", 0);
        } catch (JSONException e) {
            throw new ZSprintsException(response);
        }
        return statusCode == HttpServletResponse.SC_UNAUTHORIZED
                || (statusCode == HttpServletResponse.SC_BAD_REQUEST && (code == 7601 || code == 7700));
    }

    public String execute() throws Exception {
        HttpResponse<String> response = clientBuilder.build();
        String responseString = response.body();
        statusCode = response.statusCode();
        if (!isRetry && isTokenExpired(responseString)) {
            logger.info("Retrying the request...");
            isRetry = true;
            generateNewAccessToken();
            return execute();
        }
        if (isSuccessRequest(responseString)) {
            return responseString;
        }
        throw new ZSprintsException(new JSONObject(responseString).toString());
    }

    private void generateNewAccessToken() throws Exception {
        logger.info("New Token method invoked");
        String accessToken = null;
        HttpResponse<String> response = new RequestClient.RequestClientBuilder(
                config.getAccountsDomain() + "/oauth/v2/token", METHOD_POST)
                .setParameter("grant_type", "refresh_token")
                .setParameter("client_id", config.getClientId())
                .setParameter("client_secret", config.getClientSecret())
                .setParameter("refresh_token", config.getRefreshToken())
                .setParameter("redirect_uri", config.getRedirectURL())
                .build();

        if (response.statusCode() == HttpServletResponse.SC_OK) {
            String responseString = response.body();
            JSONObject respObj = new JSONObject(responseString);
            if (respObj.has(KEY_ACCESS_TOKEN)) {
                logger.info("New Access token created ");
                accessToken = respObj.getString(KEY_ACCESS_TOKEN);
                config.setAccessToken(accessToken);
                config.withRefreshToken(respObj.optString(KEY_REFRESH_TOKEN, config.getRefreshToken()));
                config.save();
                clientBuilder.setHeader("Authorization", "Zoho-oauthtoken " + accessToken);
                logger.info("New Token generated");
            } else {
                logger.log(Level.INFO, "Error occurred during new access token creation Error - {0}", responseString);
                throw new ZSprintsException(responseString);
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

        private RequestClient.RequestClientBuilder getClientBuilder() throws Exception {
            return new RequestClient.RequestClientBuilder(url, method, queryParam)
                    .setHeader(header)
                    .setJsonBodyContent(isJsonBodyresponse);
        }

        public ZohoClient build() throws Exception {
            constructURI();
            prependDomain();
            setDefaultHeader();
            return new ZohoClient(getClientBuilder());
        }
    }
}
