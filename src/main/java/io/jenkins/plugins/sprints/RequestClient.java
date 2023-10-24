package io.jenkins.plugins.sprints;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class RequestClient {
    private static final String CHARSET = StandardCharsets.UTF_8.name();
    private HttpRequest request;

    private RequestClient(HttpRequest request) throws Exception {
        this.request = request;
    }

    public HttpResponse<String> execute() throws Exception {
        return HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
    }

    public static class RequestClientBuilder {
        private String method, url;
        private Map<String, Object> queryParam = new HashMap<>();
        private Map<String, String> header = new HashMap<>();
        private boolean isJSONBodyContent = false;

        public RequestClientBuilder(String url, String method, Map<String, Object> queryParam) throws Exception {
            this.url = url;
            this.method = method;
            this.queryParam = queryParam;
        }

        public RequestClientBuilder(String url, String method) throws Exception {
            this.url = url;
            this.method = method;
        }

        public RequestClientBuilder setParameter(String key, Object value) {
            queryParam.put(key, value);
            return this;
        }

        public RequestClientBuilder setJsonBodyContent(boolean isJSONBodyContent) {
            this.isJSONBodyContent = isJSONBodyContent;
            return this;
        }

        public RequestClientBuilder setHeader(Map<String, String> header) {
            this.header = header;
            return this;
        }

        private String encode(String value) throws UnsupportedEncodingException {
            return URLEncoder.encode(value, CHARSET);
        }

        private String getFormattedURLWithQueryParam() {
            if (isJSONBodyContent || queryParam.isEmpty()) {
                return url;
            }

            String queryString = queryParam.entrySet().stream()
                    .map(x -> {
                        try {
                            return encode(x.getKey()) + "=" + encode(x.getValue().toString());
                        } catch (UnsupportedEncodingException e) {
                        }
                        return null;
                    })
                    .collect(Collectors.joining("&"));
            return url + "?" + queryString;
        }

        public RequestClient build() throws Exception {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(new URI(getFormattedURLWithQueryParam()))
                    .timeout(Duration.ofMinutes(5));
            if (ZohoClient.METHOD_POST.equals(method)) {
                if (isJSONBodyContent) {
                    requestBuilder.setHeader("Content-type", "application/json");
                }
                requestBuilder
                        .POST(isJSONBodyContent ? BodyPublishers.ofString(new JSONObject(queryParam).toString())
                                : BodyPublishers.noBody());
            }
            header.entrySet().forEach(x -> requestBuilder.setHeader(x.getKey(), x.getValue()));
            return new RequestClient(requestBuilder.build());
        }
    }
}
