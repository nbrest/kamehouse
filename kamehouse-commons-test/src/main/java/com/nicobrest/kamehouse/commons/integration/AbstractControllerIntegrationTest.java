package com.nicobrest.kamehouse.commons.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
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

/**
 * Abstract class to group common integration tests functionality.
 *
 * @author nbrest
 */
public abstract class AbstractControllerIntegrationTest extends AbstractIntegrationTest {

  private static final String LOGIN_CREDENTIALS_FILE =
      "/.kamehouse/config/keys/integration-test-cred.enc";
  private static final String LOGIN_URL = "/kame-house/login";
  private static final String RESPONSE_BODY = "Response body {}";
  private static final String RESPONSE_BODY_NULL = "response body is null";
  private static final String RESPONSE_BODY_EMPTY = "response body is empty";

  private HttpClient httpClient;

  /**
   * Init integration tests class logging in to the backend by default.
   */
  protected AbstractControllerIntegrationTest() {
    this(true);
  }

  /**
   * Init integration tests class.
   */
  protected AbstractControllerIntegrationTest(boolean login) {
    setHttpClient();
    if (login) {
      try {
        login();
      } catch (IOException e) {
        logger.info("Error logging in to {}", getLoginUrl());
        throw new KameHouseException("Error logging in to " + getLoginUrl());
      }
    }
  }

  /**
   * Execute a GET request.
   */
  public HttpResponse get(String url) throws IOException {
    HttpGet get = HttpClientUtils.httpGet(url);
    return getHttpClient().execute(get);
  }

  /**
   * Execute a POST request.
   */
  public <T> HttpResponse post(String url, T requestBody) throws IOException {
    HttpPost post = new HttpPost(url);
    post.setEntity(getRequestBody(requestBody));
    return getHttpClient().execute(post);
  }

  /**
   * Execute a POST request.
   */
  public HttpResponse post(String url) throws IOException {
    HttpPost post = new HttpPost(url);
    return getHttpClient().execute(post);
  }

  /**
   * Execute a PUT request.
   */
  public <T> HttpResponse put(String url, T requestBody) throws IOException {
    HttpPut put = new HttpPut(url);
    put.setEntity(getRequestBody(requestBody));
    return getHttpClient().execute(put);
  }

  /**
   * Execute a PUT request.
   */
  public HttpResponse put(String url) throws IOException {
    HttpPut put = new HttpPut(url);
    return getHttpClient().execute(put);
  }

  /**
   * Execute a DELETE request.
   */
  public HttpResponse delete(String url) throws IOException {
    HttpDelete delete = new HttpDelete(url);
    return getHttpClient().execute(delete);
  }

  /**
   * Get the http client to execute requests.
   */
  public HttpClient getHttpClient() {
    return httpClient;
  }

  /**
   * Get the request body from the entity.
   */
  public <T> HttpEntity getRequestBody(T object) throws IOException {
    byte[] requestBody = JsonUtils.toJsonByteArray(object);
    return new ByteArrayEntity(requestBody, ContentType.APPLICATION_JSON);
  }

  /**
   * Gets the response body of the request as an object of the specified class.
   */
  public static <T> T getResponseBody(HttpResponse response, Class<T> clazz)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String responseBodyString = new String(response.getEntity().getContent().readAllBytes(),
        Charsets.UTF_8);
    T responseBody;
    if (clazz == String.class) {
      responseBody = (T) responseBodyString;
    } else {
      responseBody = mapper.readValue(responseBodyString,
          mapper.getTypeFactory().constructType(clazz));
    }
    return responseBody;
  }

  /**
   * Gets the response body of the request as a list of objects of the specified class.
   */
  public static <T> List<T> getResponseBodyList(HttpResponse response, Class<T> clazz)
      throws IOException {
    String responseBodyString = new String(response.getEntity().getContent().readAllBytes(),
        Charsets.UTF_8);
    return getResponseBodyList(responseBodyString, clazz);
  }

  /**
   * Gets the response body of the request as a list of objects of the specified class.
   */
  public static <T> List<T> getResponseBodyList(String responseString, Class<T> clazz)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(responseString,
        mapper.getTypeFactory().constructCollectionType(List.class, clazz));
  }


  /**
   * Verify the response status is Created and it contains a response body.
   */
  public <T> T verifySuccessfulCreatedResponse(HttpResponse response, Class<T> clazz)
      throws IOException {
    assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
    T responseBody = getResponseBody(response, clazz);
    logger.info(RESPONSE_BODY, responseBody);
    assertNotNull(responseBody, RESPONSE_BODY_NULL);
    return responseBody;
  }

  /**
   * Verify the response status is OK and it contains a response body.
   */
  public <T> T verifySuccessfulResponse(HttpResponse response, Class<T> clazz) throws IOException {
    return verifySuccessfulResponse(response, clazz, true);
  }

  /**
   * Verify the response status is OK and it contains a response body.
   */
  public <T> T verifySuccessfulResponse(HttpResponse response, Class<T> clazz,
      boolean logResponseBody) throws IOException {
    T responseBody = getResponseBody(response, clazz);
    if (logResponseBody) {
      logger.info(RESPONSE_BODY, responseBody);
    }
    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    assertNotNull(responseBody, RESPONSE_BODY_NULL);
    return responseBody;
  }

  /**
   * Verify the response status is OK and it contains a response body.
   */
  public <T> List<T> verifySuccessfulResponseList(HttpResponse response, Class<T> clazz)
      throws IOException {
    assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    List<T> responseBody = getResponseBodyList(response, clazz);
    logger.info(RESPONSE_BODY, responseBody);
    assertNotNull(responseBody, RESPONSE_BODY_NULL);
    assertTrue(!responseBody.isEmpty(), RESPONSE_BODY_EMPTY);
    return responseBody;
  }

  /**
   * Verify the response status is in the expected list and it contains a response body of the
   * expected formats.
   */
  public void verifyResponseList(HttpResponse response, List<Integer> statuses,
      List<Class<?>> clazzes) throws IOException {
    assertTrue(statuses.contains(response.getStatusLine().getStatusCode()));
    String responseBodyString = new String(response.getEntity().getContent().readAllBytes(),
        Charsets.UTF_8);

    boolean invalidResponse = true;
    for (Class<?> clazz : clazzes) {
      try {
        List<?> responseBody = getResponseBodyList(responseBodyString, clazz);
        logger.info(RESPONSE_BODY, responseBody);
        assertNotNull(responseBody, RESPONSE_BODY_NULL);
        assertTrue(!responseBody.isEmpty(), RESPONSE_BODY_EMPTY);
        invalidResponse = false;
      } catch (UnrecognizedPropertyException e) {
        logger.info("Expected possible object mapping error. Ignoring...", e);
      }
    }
    assertFalse(invalidResponse);
  }

  /**
   * Get login url.
   */
  protected String getLoginUrl() {
    return getBaseUrl() + LOGIN_URL;
  }

  /**
   * Execute a login to the specified server.
   */
  protected void login() throws IOException {
    logger.info("Logging in to {}", getLoginUrl());
    httpClient.execute(getLoginRequest());
  }

  /**
   * Set the http client to be used in all requests.
   */
  private void setHttpClient() {
    httpClient = HttpClientUtils.getClient("", "");
  }

  /**
   * Get login credentials.
   */
  private List<NameValuePair> getLoginCredentials() {
    String loginCredentials = null;
    try {
      String loginCredentialsFile = PropertiesUtils.getUserHome() + LOGIN_CREDENTIALS_FILE;
      loginCredentials = EncryptionUtils.decryptKameHouseFileToString(loginCredentialsFile);
    } catch (KameHouseException e) {
      logger.error("Error decrypting credentials file, trying default values", e);
    }
    String[] loginCredentialsArray;
    if (loginCredentials != null) {
      loginCredentialsArray = loginCredentials.split(":");
    } else {
      logger.debug("Login credentials not found from file, setting default values for ci");
      loginCredentialsArray = new String[]{"seiya", "ikki"};
    }
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
