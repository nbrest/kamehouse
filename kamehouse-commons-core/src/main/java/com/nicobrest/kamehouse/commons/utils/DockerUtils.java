package com.nicobrest.kamehouse.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.SystemCommandStatus;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Utility class to manage all the logic related to docker containers.
 *
 * @author nbrest
 */
public class DockerUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(DockerUtils.class);
  private static final String DOCKER_CONTAINER_ENV = ".kamehouse/.kamehouse-docker-container-env";
  private static final String WINDOWS_HOME_PREFIX = "C:\\Users\\";
  private static final String LINUX_HOME_PREFIX = "/home/";
  private static final String GROOT_EXECUTE_URL =
      "/kame-house-groot/api/v1/admin/kamehouse-shell/execute.php?";
  private static final String GROOT_LOGIN_URL = "/kame-house-groot/api/v1/auth/login.php";

  private DockerUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Execute the system command on the host running the docker container.
   */
  public static SystemCommand.Output executeOnDockerHost(SystemCommand systemCommand) {
    HttpClient client = getGrootExecuteHttpClient();
    HttpGet request = HttpClientUtils.httpGet(getDockerHostGrootExecuteUrl(systemCommand));
    systemCommand.initOutputCommand();
    SystemCommand.Output commandOutput = systemCommand.getOutput();
    try {
      loginToGroot(client);
      HttpResponse response = HttpClientUtils.execRequest(client, request);
      try (InputStream resInStream = HttpClientUtils.getInputStream(response);
          BufferedReader responseReader =
              new BufferedReader(new InputStreamReader(resInStream, StandardCharsets.UTF_8))) {
        StringBuilder responseBodyStr = new StringBuilder();
        String line = "";
        while ((line = responseReader.readLine()) != null) {
          responseBodyStr.append(line);
        }
        String responseBody = responseBodyStr.toString();
        LOGGER.debug("Groot responseBody: {}", responseBody);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseBodyJson = mapper.readTree(responseBody);
        JsonNode bashConsoleOutput = responseBodyJson.get("bashConsoleOutput");
        if (bashConsoleOutput != null) {
          commandOutput.setStandardOutput(Arrays.asList(bashConsoleOutput.asText()));
          commandOutput.setStandardError(List.of(""));
          commandOutput.setExitCode(0);
          commandOutput.setStatus(SystemCommandStatus.COMPLETED.getStatus());
          return commandOutput;
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error sending groot execute request. Message: {}", e.getMessage());
    }
    commandOutput.setStandardOutput(List.of(""));
    commandOutput.setStandardError(List.of(""));
    commandOutput.setExitCode(1);
    commandOutput.setStatus(SystemCommandStatus.FAILED.getStatus());
    return commandOutput;
  }

  /**
   * Checks if it should execute the specified system command on the host running the docker
   * container.
   */
  public static boolean shouldExecuteOnDockerHost(SystemCommand systemCommand) {
    return shouldExecuteOnDockerHost(systemCommand.executeOnDockerHost());
  }

  /**
   * Checks if it should execute the specified system command on the host running the docker
   * container.
   */
  public static boolean shouldExecuteOnDockerHost(boolean isSystemCommandSetToExecuteOnHost) {
    return shouldControlDockerHost() && isSystemCommandSetToExecuteOnHost;
  }

  /**
   * Checks if it should control the docker host.
   */
  public static boolean shouldControlDockerHost() {
    return isDockerContainer() && isDockerControlHostEnabled();
  }

  /**
   * True if the host running the container is a windows host.
   */
  public static boolean isWindowsDockerHost() {
    String dockerHostOs = getDockerHostOs();
    return dockerHostOs != null
        && dockerHostOs.toLowerCase(Locale.getDefault()).startsWith("windows");
  }

  /**
   * Checks if it's a windows host or a docker host windows host when controlling the host is
   * enabled.
   */
  public static boolean isWindowsHostOrWindowsDockerHost() {
    return PropertiesUtils.isWindowsHost() || (shouldControlDockerHost() && isWindowsDockerHost());
  }

  /**
   * True if kamehouse is running on a docker container.
   */
  public static boolean isDockerContainer() {
    return PropertiesUtils.getBooleanProperty("IS_DOCKER_CONTAINER");
  }

  /**
   * True if the container is running with configured to control the host.
   */
  public static boolean isDockerControlHostEnabled() {
    return PropertiesUtils.getBooleanProperty("DOCKER_CONTROL_HOST");
  }

  /**
   * Get the docker host's auth header.
   */
  public static String getDockerHostAuth() {
    return PropertiesUtils.getProperty("DOCKER_HOST_AUTH");
  }

  /**
   * Get the docker host's ip address.
   */
  public static String getDockerHostIp() {
    return PropertiesUtils.getProperty("DOCKER_HOST_IP");
  }

  /**
   * Get the docker host's port.
   */
  public static String getDockerHostPort() {
    return PropertiesUtils.getProperty("DOCKER_HOST_PORT");
  }

  /**
   * Get the docker host's playlists path.
   */
  public static String getDockerHostPlaylistPath() {
    return PropertiesUtils.getProperty("DOCKER_HOST_PLAYLIST_PATH");
  }

  /**
   * Get the docker host's hostname.
   */
  public static String getDockerHostHostname() {
    return PropertiesUtils.getProperty("DOCKER_HOST_HOSTNAME");
  }

  /**
   * Get the username on the host running the container.
   */
  public static String getDockerHostUsername() {
    return PropertiesUtils.getProperty("DOCKER_HOST_USERNAME");
  }

  /**
   * Get the user's home on the host running the container.
   */
  public static String getDockerHostUserHome() {
    String username = getDockerHostUsername();
    if (isWindowsDockerHost()) {
      return WINDOWS_HOME_PREFIX + username;
    } else {
      return LINUX_HOME_PREFIX + username;
    }
  }

  /**
   * Get the properties from the docker container (if it's running in a container).
   */
  public static Properties getDockerContainerProperties() {
    Properties dockerProperties = new Properties();
    try {
      String path = PropertiesUtils.getUserHome() + File.separator + DOCKER_CONTAINER_ENV;
      File dockerContainerEnvFile = new File(path);
      if (!dockerContainerEnvFile.exists()) {
        LOGGER.debug("Docker container env file doesn't exists. Running outside a container");
        return dockerProperties;
      }
      Resource propertiesResource = new FileSystemResource(path);
      dockerProperties = PropertiesLoaderUtils.loadProperties(propertiesResource);
      return dockerProperties;
    } catch (IOException e) {
      LOGGER.warn("Error loading docker container properties.", e);
    }
    return dockerProperties;
  }

  /**
   * If running outside a docker container return the hostname. For docker containers return the IP
   * of the host, if control host is enabled.
   */
  public static String getHostname() {
    if (shouldControlDockerHost()) {
      return getDockerHostIp();
    }
    return PropertiesUtils.getHostname();
  }

  /**
   * Get the user home either from the container or from the host depending if it's set to control
   * the host or not.
   */
  public static String getUserHome() {
    return getUserHome(true);
  }

  /**
   * Get the user home either from the container or from the host depending if it's set to control
   * the host or not.
   */
  public static String getUserHome(boolean executeOnDockerHost) {
    if (DockerUtils.shouldControlDockerHost() && executeOnDockerHost) {
      return DockerUtils.getDockerHostUserHome();
    }
    return PropertiesUtils.getUserHome();
  }

  /**
   * Get http client to send groot execute request.
   */
  private static HttpClient getGrootExecuteHttpClient() {
    String[] loginCredentials = getLoginCredentials();
    return HttpClientUtils.getClient(loginCredentials[0], loginCredentials[0], true);
  }

  /**
   * Get login credentials to docker host.
   */
  private static String[] getLoginCredentials() {
    String basicAuth = getDockerHostAuth();
    try {
      byte[] basicAuthByteArray = Base64.getDecoder().decode(basicAuth);
      if (basicAuthByteArray == null) {
        throw new KameHouseServerErrorException("Unable to decode docker host auth");
      }
      String basicAuthDecoded = new String(basicAuthByteArray, StandardCharsets.UTF_8);
      if (StringUtils.isEmpty(basicAuthDecoded) || !basicAuthDecoded.contains(":")) {
        throw new KameHouseServerErrorException("Invalid value for docker host auth");
      }
      return basicAuthDecoded.split(":");
    } catch (IllegalArgumentException e) {
      throw new KameHouseServerErrorException("Unable to decode docker host auth");
    }
  }

  /**
   * Build url to execute system command on remote host via groot.
   */
  private static String getDockerHostGrootExecuteUrl(SystemCommand systemCommand) {
    String host = getDockerHostIp();
    String port = getDockerHostPort();
    StringBuilder sb = new StringBuilder("https://");
    sb.append(host).append(":").append(port).append(GROOT_EXECUTE_URL).append("script=");
    sb.append(systemCommand.getShellScriptScript());
    String args = systemCommand.getShellScriptScriptArgs();
    if (!StringUtils.isEmpty(args)) {
      String urlEncodedArgs = HttpClientUtils.urlEncode(args.trim());
      sb.append("&args=").append(urlEncodedArgs);
    }
    return sb.toString().trim();
  }

  /**
   * Get the OS of the docker host.
   */
  private static String getDockerHostOs() {
    return PropertiesUtils.getProperty("DOCKER_HOST_OS");
  }

  /**
   * Login to kamehouse-groot to send the execute request.
   */
  private static void loginToGroot(HttpClient client) throws IOException {
    String host = getDockerHostIp();
    String port = getDockerHostPort();
    StringBuilder loginUrl = new StringBuilder("https://");
    loginUrl.append(host).append(":").append(port).append(GROOT_LOGIN_URL);
    String[] loginCredentials = getLoginCredentials();
    List<NameValuePair> loginBody = new ArrayList<>();
    loginBody.add(new BasicNameValuePair("username", loginCredentials[0]));
    loginBody.add(new BasicNameValuePair("password", loginCredentials[1]));
    HttpPost login = new HttpPost(loginUrl.toString());
    login.setEntity(new UrlEncodedFormEntity(loginBody));
    HttpClientUtils.execRequest(client, login);
  }
}
