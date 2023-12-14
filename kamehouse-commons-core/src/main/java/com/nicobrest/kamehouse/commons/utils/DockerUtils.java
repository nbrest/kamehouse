package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
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

  private DockerUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Execute the system command on the host running the docker container.
   */
  public static SystemCommand.Output executeOnDockerHost(SystemCommand systemCommand) {
    String host = getDockerHostIp();
    String username = getDockerHostUsername();
    return SshClientUtils.execute(host, username, systemCommand);
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
   * Get the docker host's ip address.
   */
  public static String getDockerHostIp() {
    return PropertiesUtils.getProperty("DOCKER_HOST_IP");
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
   * Get the OS of the docker host.
   */
  private static String getDockerHostOs() {
    return PropertiesUtils.getProperty("DOCKER_HOST_OS");
  }
}
