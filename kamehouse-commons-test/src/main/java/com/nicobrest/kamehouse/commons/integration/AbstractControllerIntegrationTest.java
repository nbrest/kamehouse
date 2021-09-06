package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to group common integration tests functionality.
 *
 * @author nbrest
 */
public abstract class AbstractControllerIntegrationTest {

  private static final String LOGIN_CREDENTIALS_FILE =
      "/home-synced/.kamehouse/integration-test-cred.enc";

  private static final String LOGIN_URL = "/kame-house/login";

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  private String protocol;
  private String hostname;
  private String port;

  private HttpClient httpClient;

  /**
   * Get the webapp to connect to on the integration tests. Ej. "kame-house-admin".
   */
  public abstract String getWebapp();

  /**
   * Init integration tests class.
   */
  public AbstractControllerIntegrationTest() {
    protocol = PropertiesUtils.getProperty("integration.tests.protocol", "http://");
    hostname = PropertiesUtils.getProperty("integration.tests.hostname", "localhost");
    port = PropertiesUtils.getProperty("integration.tests.port", "9980");
    logger.info("Base url for integration tests: " + getWebappUrl());
    setHttpClient();
    try {
      login();
    } catch (IOException e) {
      logger.info("Error logging in to {}", getLoginUrl());
      throw new KameHouseException("Error logging in to " + getLoginUrl());
    }
  }

  /**
   * Execute a GET request.
   */
  protected HttpResponse get(String url) throws IOException {
    HttpGet get = HttpClientUtils.httpGet(url);
    return getHttpClient().execute(get);
  }

  /**
   * Execute a POST request.
   */
  protected <T> HttpResponse post(String url, T requestBody) throws IOException {
    HttpPost post = new HttpPost(url);
    post.setEntity(getRequestBody(requestBody));
    return getHttpClient().execute(post);
  }

  /**
   * Execute a POST request.
   */
  protected <T> HttpResponse post(String url) throws IOException {
    HttpPost post = new HttpPost(url);
    return getHttpClient().execute(post);
  }

  /**
   * Execute a PUT request.
   */
  protected <T> HttpResponse put(String url, T requestBody) throws IOException {
    HttpPut put = new HttpPut(url);
    put.setEntity(getRequestBody(requestBody));
    return getHttpClient().execute(put);
  }

  /**
   * Execute a DELETE request.
   */
  protected HttpResponse delete(String url) throws IOException {
    HttpDelete delete = new HttpDelete(url);
    return getHttpClient().execute(delete);
  }

  /**
   * Get the http client to execute requests.
   */
  protected HttpClient getHttpClient() {
    return httpClient;
  }

  /**
   * Get the base url for all requests.
   */
  protected String getBaseUrl() {
    return protocol + hostname + ":" + port;
  }

  /**
   * Get the url for the specified webapp.
   */
  protected String getWebappUrl() {
    return getBaseUrl() + "/" + getWebapp();
  }

  /**
   * Get the request body from the entity.
   */
  protected <T> HttpEntity getRequestBody(T object) throws IOException {
    byte[] requestBody = JsonUtils.toJsonByteArray(object);
    return new ByteArrayEntity(requestBody, ContentType.APPLICATION_JSON);
  }

  /**
   * Gets the response body of the request as an object of the specified class.
   */
  protected static <T> T getResponseBody(HttpResponse response, Class<T> clazz)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String responseBodyString = new String(response.getEntity().getContent().readAllBytes(),
        Charsets.UTF_8);
    T responseBody = mapper.readValue(responseBodyString,
        mapper.getTypeFactory().constructType(clazz));
    return responseBody;
  }

  /**
   * Gets the response body of the request as a list of objects of the specified class.
   */
  protected static <T> List<T> getResponseBodyList(HttpResponse response, Class<T> clazz)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String responseBodyString = new String(response.getEntity().getContent().readAllBytes(),
        Charsets.UTF_8);
    List<T> responseBody = mapper.readValue(responseBodyString,
        mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    return responseBody;
  }


  /**
   * Verify the response status is Created and it contains a response body.
   */
  protected <T> T verifySuccessfulCreatedResponse(HttpResponse response, Class<T> clazz)
      throws IOException {
    assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    T responseBody = getResponseBody(response, clazz);
    assertNotNull(responseBody);
    logger.info("Response body {}", responseBody);
    return responseBody;
  }

  /**
   * Verify the response status is OK and it contains a response body.
   */
  protected <T> T verifySuccessfulResponse(HttpResponse response, Class<T> clazz)
      throws IOException {
    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    T responseBody = getResponseBody(response, clazz);
    assertNotNull(responseBody);
    logger.info("Response body {}", responseBody);
    return responseBody;
  }

  /**
   * Verify the response status is OK and it contains a response body.
   */
  protected <T> List<T> verifySuccessfulResponseList(HttpResponse response, Class<T> clazz)
      throws IOException {
    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    List<T> responseBody = getResponseBodyList(response, clazz);
    assertNotNull(responseBody);
    assertTrue(responseBody.size() > 0);
    logger.info("Response body {}", responseBody);
    return responseBody;
  }

  /**
   * Set the http client to be used in all requests.
   */
  private void setHttpClient() {
    httpClient = HttpClientUtils.getClient("", "");
  }

  /**
   * Get login url.
   */
  private String getLoginUrl() {
    return getBaseUrl() + LOGIN_URL;
  }

  /**
   * Execute a login to the specified server.
   */
  private void login() throws IOException {
    logger.info("Logging in to {}", getLoginUrl());
    httpClient.execute(getLoginRequest());
  }

  /**
   * Get login credentials.
   */
  private List<NameValuePair> getLoginCredentials() {
    String loginCredentialsFile = PropertiesUtils.getUserHome() + LOGIN_CREDENTIALS_FILE;
    String loginCredentials = EncryptionUtils.decryptKameHouseFileToString(loginCredentialsFile);
    String[] loginCredentialsArray = loginCredentials.split(":");
    List<NameValuePair> loginBody = new ArrayList<>();
    loginBody.add(new BasicNameValuePair("username", loginCredentialsArray[0]));
    loginBody.add(new BasicNameValuePair("password", loginCredentialsArray[1]));
    return loginBody;
  }

  /**
   * Get login request.
   */
  private HttpPost getLoginRequest() throws UnsupportedEncodingException {
    List<NameValuePair> loginCredentials = getLoginCredentials();
    HttpPost login = new HttpPost(getLoginUrl());
    login.setEntity(new UrlEncodedFormEntity(loginCredentials));
    return login;
  }
}
