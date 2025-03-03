package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

/**
 * Utility class to perform HTTP requests to other services from the backend.
 *
 * @author nbrest
 */
public class HttpClientUtils {

  private static final int TIME_TO_LIVE = 180;
  private static final int MAX_CONNECTIONS = 1000;
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

  private HttpClientUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Creates an instance of HttpClient with the provided credentials.
   */
  public static HttpClient getClient(String username, String password) {
    return getClient(username, password, false);
  }

  /**
   * Creates an instance of HttpClient with the provided credentials.
   */
  public static HttpClient getClient(String username, String password, boolean skipSslCheck) {
    if (username == null) {
      username = "";
    }
    if (password == null) {
      password = "";
    }
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
    credentialsProvider.setCredentials(AuthScope.ANY, credentials);
    if (skipSslCheck) {
      try {
        return HttpClientBuilder.create()
            .setDefaultCredentialsProvider(credentialsProvider)
            .setSSLContext(
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .disableConnectionState()
            .disableAutomaticRetries()
            .setConnectionTimeToLive(TIME_TO_LIVE, TimeUnit.SECONDS)
            .setMaxConnPerRoute(MAX_CONNECTIONS)
            .setMaxConnTotal(MAX_CONNECTIONS)
            .build();
      } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
        LOGGER.error("Unable to create http client");
        throw new KameHouseServerErrorException(e.getMessage());
      }
    }
    return HttpClientBuilder.create()
        .setDefaultCredentialsProvider(credentialsProvider)
        .disableConnectionState()
        .disableAutomaticRetries()
        .setConnectionTimeToLive(TIME_TO_LIVE, TimeUnit.SECONDS)
        .setMaxConnPerRoute(MAX_CONNECTIONS)
        .setMaxConnTotal(MAX_CONNECTIONS)
        .build();
  }

  /**
   * Checks if the http response contains a response body.
   */
  public static boolean hasResponseBody(HttpResponse response) throws IOException {
    return response.getEntity() != null && response.getEntity().getContent() != null;
  }

  /**
   * Returns the response content as an InputStream.
   */
  public static InputStream getInputStream(HttpResponse response) throws IOException {
    return response.getEntity().getContent();
  }

  /**
   * Returns the status line from the response.
   */
  public static StatusLine getStatusLine(HttpResponse response) {
    return response.getStatusLine();
  }

  /**
   * Returns the status code from the response.
   */
  public static int getStatusCode(HttpResponse response) {
    return getStatusLine(response).getStatusCode();
  }

  /**
   * Returns the specified header from the response or null if not found.
   */
  public static String getHeader(HttpResponse response, String key) {
    if (response != null) {
      Header header = response.getFirstHeader(key);
      if (header != null) {
        return header.getValue();
      }
    }
    return null;
  }

  /**
   * Returns all header from the request or null if not found.
   */
  public static Header[] getAllHeaders(HttpRequest request) {
    return request.getAllHeaders();
  }

  /**
   * Returns all header from the request or null if not found.
   */
  public static Header[] getAllHeaders(HttpResponse response) {
    return response.getAllHeaders();
  }

  /**
   * Returns true if the http request has headers.
   */
  public static boolean hasHeaders(HttpRequest request) {
    return getAllHeaders(request) == null || getAllHeaders(request).length > 0;
  }

  /**
   * Returns true if the http request has headers.
   */
  public static boolean hasHeaders(HttpResponse response) {
    return getAllHeaders(response) == null || getAllHeaders(response).length > 0;
  }

  /**
   * Creates an HTTP Get request to the specified URL.
   */
  public static HttpGet httpGet(String url) {
    return new HttpGet(url);
  }

  /**
   * Executes the HTTP request to the specified HttpClient.
   */
  public static HttpResponse execRequest(HttpClient client, HttpUriRequest request)
      throws IOException {
    return client.execute(request);
  }

  /**
   * Encode the specified parameter to use as a URL.
   */
  public static String urlEncode(String parameter) {
    try {
      return UriUtils.encodeQuery(parameter, Charsets.UTF_8);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Failed to encode parameter: {}", parameter, e);
      return null;
    }
  }

  /**
   * Decode the specified url.
   */
  public static String urlDecode(String url) {
    if (url == null) {
      return null;
    }
    try {
      return UriUtils.decode(url, StandardCharsets.UTF_8.name());
    } catch (IllegalArgumentException e) {
      LOGGER.error("Failed to decode url: {}", url, e);
      return null;
    }
  }

  /**
   * Add url parameters to the http GET request and return the generated URI.
   */
  public static URI addUrlParameters(HttpGet request, Map<String, String> parameters)
      throws URISyntaxException {
    URIBuilder uriBuilder = new URIBuilder(request.getURI());
    for (Entry<String, String> paramEntry : parameters.entrySet()) {
      uriBuilder.addParameter(paramEntry.getKey(), paramEntry.getValue());
    }
    URI uri = uriBuilder.build();
    request.setURI(uri);
    return uri;
  }

  /**
   * Log the response code received from tennis world.
   */
  public static void logHttpResponseCode(HttpResponse httpResponse) {
    LOGGER.info("Response code: {}", HttpClientUtils.getStatusLine(httpResponse));
  }

  /**
   * Log response headers.
   */
  public static void logResponseHeaders(HttpResponse httpResponse) {
    LOGGER.debug("Response headers:");
    if (!hasHeaders(httpResponse)) {
      LOGGER.debug("No response headers set");
    } else {
      for (Header header : getAllHeaders(httpResponse)) {
        LOGGER.debug("{} : {}", header.getName(), header.getValue());
      }
    }
  }

  /**
   * Log response body.
   */
  public static void logResponseBody(String responseBody) {
    if (!StringUtils.isEmpty(responseBody) && LOGGER.isTraceEnabled()) {
      LOGGER.trace("Response body: {}", StringUtils.sanitize(responseBody));
    }
  }

  /**
   * Log request headers.
   */
  public static void logRequestHeaders(HttpRequest httpRequest) {
    LOGGER.debug("Request headers:");
    if (!hasHeaders(httpRequest)) {
      LOGGER.debug("No request headers set");
    } else {
      for (Header header : getAllHeaders(httpRequest)) {
        LOGGER.debug("{} : {}", header.getName(), header.getValue());
      }
    }
  }

  /**
   * Log request body.
   */
  public static void logRequestBody(String requestBody) {
    if (!StringUtils.isEmpty(requestBody) && LOGGER.isTraceEnabled()) {
      LOGGER.trace("Request body: {}", StringUtils.sanitize(requestBody));
    }
  }
}
