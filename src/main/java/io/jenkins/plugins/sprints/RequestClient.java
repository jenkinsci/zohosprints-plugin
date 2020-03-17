package io.jenkins.plugins.sprints;

import hudson.ProxyConfiguration;
import io.jenkins.plugins.util.OAuthUtil;
import io.jenkins.plugins.util.Util;
import jenkins.model.Jenkins;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class RequestClient {

    private static final Logger LOGGER = Logger.getLogger(RequestClient.class.getName());
    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";
    public static final String METHOD_DELETE = "delete";
    public  static final String CHARSET = "UTF-8";
    private String url = null;
    private String method = null;
    private Map<String, Object> param = new HashMap<>();
    private Map<String, String> header = new HashMap<>();
    private Map<String, AttachmentUtil> attachment = new HashMap<>();
    private boolean isJSONBodyContent;
    private JSONArray jsonArrayBodyContent = null;
    private JSONObject paramJson = null;

    /**
     *
     * @param paramMap Query param of API
     * @return Instance of Class
     */
    public RequestClient setParam(final Map<String, Object> paramMap) {
        this.param = paramMap;
        return this;
    }

    /**
     *
     * @return JSONArray
     */
    public JSONArray getJsonArrayBodyContent() {
        return jsonArrayBodyContent;
    }

    /**
     *
     * @param jsonArrayBodyContentArr JSONType param
     */
    public void setJsonArrayBodyContent(final JSONArray jsonArrayBodyContentArr) {
        this.jsonArrayBodyContent = jsonArrayBodyContentArr;
    }

    /**
     *
     * @param jsonBodyContent JSONType param
     * @return Instance of Class
     */
    public RequestClient setJSONBodyContent(final boolean jsonBodyContent) {
        isJSONBodyContent = jsonBodyContent;
        return this;
    }

    /**
     *
     * @param fromAttachmentMap attachment File
     * @return Instance of Class
     */
    public RequestClient setAttachment(final Map<String, AttachmentUtil> fromAttachmentMap) {
        this.attachment = fromAttachmentMap;
        return this;
    }

    /**
     *
     * @param fromurl Sprints API
     * @param frommethod Type of API call
     */
    public RequestClient(String fromurl, String frommethod) {
        this.url = fromurl;
        this.method = frommethod;
    }

    /**
     *
     * @param fromurl Sprints API
     * @param frommethod Type of API call
     * @param fromparam Query param of API
     * @param fromparamJson JSONType param
     */
//    public RequestClient(final String fromurl, final String frommethod, final Map<String, Object> fromparam, final Object fromparamJson) {
//        this.url = fromurl;
//        this.method = frommethod;
//        this.param = fromparam;
//        if (fromparamJson instanceof JSONArray) {
//            this.jsonArrayBodyContent = (JSONArray) fromparamJson;
//        } else if (fromparamJson instanceof JSONObject) {
//            this.paramJson = (JSONObject) fromparamJson;
//        }
//        this.isJSONBodyContent = true;
//    }

    /**
     *
     * @param fromurl Sprints API
     * @param frommethod Type of API call
     * @param fromparam Query param of API
     */
    public RequestClient(final String fromurl, final String frommethod, final Map<String, Object> fromparam) {
        this.url = fromurl;
        this.method = frommethod;
        this.param = fromparam;
    }

    /**
     *
     * @param fromurl Sprints API
     * @param frommethod Type of API call
     * @param fromjsonArrayBodyContent JSONType param
     */
    public RequestClient(final String fromurl, final String frommethod, final JSONArray fromjsonArrayBodyContent) {
        this.url = fromurl;
        this.method = frommethod;
        this.jsonArrayBodyContent = fromjsonArrayBodyContent;
        this.isJSONBodyContent = true;
    }

    /**
     *
     * @param fromurl Sprints API
     * @param frommethod Type of API call
     * @param fromparam Query param of API
     * @param fromheader Header param of API
     */
    public RequestClient(final String fromurl, final String frommethod, final Map<String, Object> fromparam, final Map<String, String> fromheader) {
        this.url = fromurl;
        this.method = frommethod;
        this.param = fromparam;
        this.header = fromheader;
    }

    /**
     *
     * @param reqobject RequestBase Object
     * @return HttpEntityEnclosingRequestBase function
     */
    private HttpEntityEnclosingRequestBase setJSONBodyEntity(final HttpEntityEnclosingRequestBase reqobject) {
        Object jsonBodyContent = param != null && !param.isEmpty() ? new JSONObject(param) : jsonArrayBodyContent;
        StringEntity entity = new StringEntity(jsonBodyContent.toString(), ContentType.APPLICATION_JSON);
        LOGGER.info(jsonBodyContent.toString());
        reqobject.setEntity(entity);
        return reqobject;
    }

    /**
     *
     * @return HttpUriRequest function
     * @throws Exception  Throws when any error occurs
     */
    private HttpUriRequest getMethod() throws Exception {

        if (method != null) {
            if (method.equals(METHOD_GET)) {
                HttpGet get = new HttpGet(url);
                if (!param.isEmpty()) {
                    get = constructUrl(get);
                }
                return get;
            } else if (method.equals(METHOD_POST)) {
                HttpPost post = new HttpPost(url);
                if (!param.isEmpty()) {
                    setEntity(post);
                }
                return post;
            } else {
                HttpDelete delete = new HttpDelete(url);
                if(isJSONBodyContent){
                    HttpDeleteWithBody deleteWithBody = new HttpDeleteWithBody(url);
                    deleteWithBody = (HttpDeleteWithBody) setJSONBodyEntity(deleteWithBody);
                    return deleteWithBody;
                } else if (!param.isEmpty()) {
                    delete = constructUrl(delete);
                }
                return delete;
            }
        }
        return null;
    }

    /**
     *
     * @param httpreq httpUriRequest Object
     * @return HttpUriRequest function
     * @throws Exception Throws when any error occurs
     */
    private HttpUriRequest setHeader(HttpUriRequest httpreq) throws Exception {
        Map<String, String> headerMap = this.header;

        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpreq.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpreq;
    }

    public void setOAuthHeader() {
        header = OAuthUtil.getOAuthHeader();
    }

    /**
     *
     * @return String format of response
     * @throws Exception Throws when any error occurs
     */
    public String execute() throws  Exception {
        int connectionTimeOut = 30000;
        int socketTimeOut = 30000;
        String resp = "";
        HttpUriRequest request = this.getMethod();
        request = this.setHeader(request);
        if (isJSONBodyContent) {
            request.setHeader("Content-type", ContentType.APPLICATION_JSON.getMimeType()); //NO I18N
        } else if(request.getHeaders("Content-type") == null) {//NO I18N
            request.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8"); //no i18n
        }
        RequestConfig config = RequestConfig
                .custom()
                .setConnectTimeout(connectionTimeOut)
                .setSocketTimeout(socketTimeOut)
                .build();
        HttpClientBuilder builder = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config);

        if (Util.isProxyConfigured()) {
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            String hosturl = proxy.name;
            int port = proxy.port;
            String uname = proxy.getUserName();
            String password = proxy.getPassword();
            HttpHost host = new HttpHost(hosturl, port);
            builder = builder.useSystemProperties();
            builder.setProxy(host);
            if(uname != null && password != null){
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                AuthScope authScope = new AuthScope(host.getHostName(), port);
                credentialsProvider.setCredentials(authScope, new UsernamePasswordCredentials(uname, password));
                builder.setDefaultCredentialsProvider(credentialsProvider);
                builder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

            }
        }
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;

        try {
            client = builder.build();
            response = client.execute(request);
            int respCode = response.getStatusLine().getStatusCode();
            LOGGER.log(Level.INFO, "Status code {0}", respCode);
            HttpEntity reponseEntity = response.getEntity();
            resp = getString(reponseEntity.getContent());
            EntityUtils.consume(reponseEntity);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "", e);
        } finally {
            if(client != null) {
                client.close();
            }
            if(response != null) {
                response.close();
            }
        }
        return resp;
    }

    /**
     *
     * @param get HttpGet Object
     * @return HttpGet function
     */
    private HttpGet  constructUrl(HttpGet get) {
        List<NameValuePair> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            list.add(new BasicNameValuePair(key, value.toString()));
        }
        url = url + "?" + URLEncodedUtils.format(list, CHARSET);
        return new HttpGet(url);
    }

    /**
     *
     * @param delete HttpDelete Object
     * @return HttpDelete function
     */
    private HttpDelete constructUrl(HttpDelete delete) {
        List<NameValuePair> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            list.add(new BasicNameValuePair(key, value.toString()));
        }
        url = url + "?" + URLEncodedUtils.format(list, CHARSET);
        return new HttpDelete(url);
    }

    /**
     *
     * @param post HttpPost Object
     * @throws UnsupportedEncodingException Throws when unsupported Encoding happens
     * @throws JSONException Throws when JSON related error happen
     */
    private void setEntity(HttpPost post) throws UnsupportedEncodingException, JSONException {
        if (this.attachment != null && !this.attachment.isEmpty()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if (param != null && !param.isEmpty()) {
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    builder.addTextBody(key, value.toString(), ContentType.TEXT_PLAIN);
                }
            }

            for (Map.Entry<String, AttachmentUtil> entry : attachment.entrySet()) {
                String key = entry.getKey();
                AttachmentUtil ip = entry.getValue();
                builder.addBinaryBody(key, ip.getInputStream(), ContentType.APPLICATION_OCTET_STREAM, ip.getName());
            }
            HttpEntity entity = builder.build();
            post.setEntity(entity);
        } else {
            if (param != null && !param.isEmpty()) {
                List<NameValuePair> entityList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    entityList.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));

                }
                post.setEntity(new UrlEncodedFormEntity(entityList));
            }
        }

    }

    /**
     *
     * @param stream Stream of the response
     * @return String format of response
     * @throws IOException Throws when error occurs at read/write
     */
    private String getString(final InputStream stream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];

            for (int length = 0; (length = stream.read(buffer)) > 0;) {
                result.write(buffer, 0, length);
            }

            return result.toString(CHARSET); //no i18n

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "RequestClient_Error_while_fetching_content=>", e);
        } finally {
            stream.close();
        }
        return null;
    }

    private class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        private static final String METHOD_NAME = "DELETE";//no i18n
        public String getMethod() { return METHOD_NAME; }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }
    }
}
