package com.nicobrest.kamehouse.main.utils;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpResponse;
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

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class to perform HTTP requests to other services from the backend.
 * 
 * @author nbrest
 *
 */
public class HttpClientUtils {

  private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

  private HttpClientUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Creates an instance of HttpClient with the provided credentials.
   */
  public static HttpClient getClient(String username, String password) {
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
    credentialsProvider.setCredentials(AuthScope.ANY, credentials);
    return HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
  }

  /**
   * Returns the response content as an InputStream.
   */
  public static InputStream getInputStreamFromResponse(HttpResponse response) throws IOException {
    return response.getEntity().getContent();
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
  public static HttpResponse executeRequest(HttpClient client, HttpUriRequest request)
      throws IOException {
    return client.execute(request);
  }

  /**
   * Encode the specified parameter to use as a URL.
   */
  public static String urlEncode(String parameter) {
    try {
      return URIUtil.encodeQuery(parameter);
    } catch (URIException | IllegalArgumentException e) {
      logger.error("Failed to encode parameter: {}", parameter);
      return null;
    }
  }
}
