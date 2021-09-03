package com.nicobrest.kamehouse.commons.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to group common integration tests functionality.
 *
 * @author nbrest
 */
public class AbstractControllerIntegrationTest {

  private static final String LOGIN_CREDENTIALS_FILE =
      "/home-synced/.kamehouse/integration-test-cred.enc";

  private static final String LOGIN_URL = "/kame-house/login";

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static String protocol = "http://";
  private static String hostname = "localhost";
  private static String port = "9980";

  private HttpClient httpClient;

  public AbstractControllerIntegrationTest() {
    setHttpClient();
  }

  /**
   * Init tests.
   */
  @BeforeEach
  public void beforeTest() throws IOException {
    login();
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
  protected static String getBaseUrl() {
    return protocol + hostname + ":" + port;
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
