package com.nicobrest.kamehouse.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
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
    if (username == null) {
      username = "";
    }
    if (password == null) {
      password = "";
    }
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
    credentialsProvider.setCredentials(AuthScope.ANY, credentials);
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
    return response.getStatusLine().getStatusCode();
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
      LOGGER.error("Failed to encode parameter: " + parameter, e);
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
}
