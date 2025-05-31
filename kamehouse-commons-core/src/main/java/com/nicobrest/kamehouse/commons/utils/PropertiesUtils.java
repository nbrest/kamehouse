package com.nicobrest.kamehouse.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Utility class to manage the application properties.
 *
 * @author nbrest
 */
public class PropertiesUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);
  private static final boolean IS_WINDOWS_HOST = setIsWindowsHost();
  private static final Properties properties = new Properties();
  private static final String BUILD_VERSION_PROPERTY = "kamehouse.build.version";

  static {
    loadAllPropertiesFiles();
    loadBuildVersion();
    loadBuildDate();
    loadGitCommitHash();
  }

  private PropertiesUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Sets the IS_WINDOWS_HOST variable.
   */
  private static boolean setIsWindowsHost() {
    return System.getProperty("os.name").toLowerCase(Locale.getDefault()).startsWith("windows");
  }

  /**
   * Returns true if the application is running on a windows host, false otherwise.
   */
  public static boolean isWindowsHost() {
    return IS_WINDOWS_HOST;
  }

  /**
   * Returns the home of the user running the application server.
   */
  public static String getUserHome() {
    return System.getProperty("user.home");
  }

  /**
   * Returns the hostname of the server.
   */
  public static String getHostname() {
    if (isWindowsHost()) {
      String computerName = System.getenv("COMPUTERNAME");
      if (computerName != null) {
        return computerName.toLowerCase(Locale.getDefault());
      }
      return "INVALID_HOSTNAME";
    } else {
      try (BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(
                  Runtime.getRuntime().exec("hostname").getInputStream(),
                  StandardCharsets.UTF_8))) {
        return reader.readLine();
      } catch (IOException e) {
        LOGGER.error("Error getting hostname.", e);
        return "INVALID_HOSTNAME";
      }
    }
  }

  /**
   * Gets the current module name (ej: admin, media, tennisworld, testmodule, ui, vlcrc) as defined
   * in kamehouse.properties.
   */
  public static String getModuleName() {
    return properties.getProperty("module.name", "MODULE_NAME_NOT_SET");
  }

  /**
   * Gets the specified property from the commons/kamehouse application properties.
   */
  public static String getProperty(String propertyName) {
    return properties.getProperty(propertyName);
  }

  /**
   * Gets the specified property from the commons/kamehouse application properties.
   */
  public static String getProperty(String propertyName, String defaultValue) {
    String value = properties.getProperty(propertyName);
    if (value == null || value.startsWith("${filter.")) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Get the boolean value of a property. Returns false if the property is not set.
   */
  public static boolean getBooleanProperty(String propertyName) {
    return Boolean.valueOf(getProperty(propertyName, "false"));
  }

  /**
   * Loads all properties files.
   */
  private static void loadAllPropertiesFiles() {
    loadPropertiesFile("commons.properties");
    loadPropertiesFile("kamehouse.properties");
    loadDockerContainerProperties();
  }

  /**
   * Loads the specified properties file.
   */
  private static void loadPropertiesFile(String filename) {
    try {
      Resource propertiesResource = new ClassPathResource("/" + filename);
      Properties loadedProperties = PropertiesLoaderUtils.loadProperties(propertiesResource);
      properties.putAll(loadedProperties);
    } catch (IOException e) {
      LOGGER.error("Error loading " + filename + " files.", e);
    }
  }

  /**
   * Loads properties from the docker container (if it's running in a container).
   */
  private static void loadDockerContainerProperties() {
    properties.putAll(DockerUtils.getDockerContainerProperties());
  }

  /**
   * Loads the git commit hash into the properties, if it's available.
   */
  protected static void loadGitCommitHash() {
    try {
      String buildVersion = getProperty(BUILD_VERSION_PROPERTY);
      if (StringUtils.isEmpty(buildVersion)) {
        LOGGER.warn("Build version not available, so skipping getting git hash");
        return;
      }
      Resource gitCommitHashResource = new ClassPathResource("/git-commit-hash.cfg");
      InputStream gitCommitHashInputStream = gitCommitHashResource.getInputStream();
      String content = IOUtils.toString(gitCommitHashInputStream,
          StandardCharsets.UTF_8.name());
      if (content == null) {
        LOGGER.error("git commit hash is empty");
        return;
      }
      if (!content.startsWith("GIT_COMMIT_HASH=")) {
        LOGGER.error("Invalid format of git commit hash");
        return;
      }
      String gitCommitHash = content.split("=")[1].trim();
      String updatedBuildVersion = buildVersion + "-" + gitCommitHash;
      properties.put(BUILD_VERSION_PROPERTY, updatedBuildVersion);
    } catch (IOException e) {
      LOGGER.error("Error loading kamehouse git commit hash into properties", e);
    }
  }

  /**
   * Loads the build version into the properties, if it's available.
   */
  protected static void loadBuildVersion() {
    try {
      Resource buildVersionResource = new ClassPathResource("/build-version.cfg");
      InputStream buildVersionInputStream = buildVersionResource.getInputStream();
      String content = IOUtils.toString(buildVersionInputStream,
          StandardCharsets.UTF_8.name());
      if (content == null) {
        LOGGER.error("Error loading kamehouse build version into properties");
        return;
      }
      if (!content.startsWith("BUILD_VERSION=")) {
        LOGGER.error("Invalid format of build version");
        return;
      }
      String buildVersion = content.split("=")[1].trim();
      properties.put(BUILD_VERSION_PROPERTY, buildVersion);
    } catch (IOException e) {
      LOGGER.error("Error loading kamehouse build version into properties", e);
    }
  }

  /**
   * Loads the build date into the properties, if it's available.
   */
  protected static void loadBuildDate() {
    try {
      Resource buildDateResource = new ClassPathResource("/build-date.cfg");
      InputStream buildDateInputStream = buildDateResource.getInputStream();
      String content = IOUtils.toString(buildDateInputStream, StandardCharsets.UTF_8.name());
      if (content == null) {
        LOGGER.error("Error loading kamehouse build date into properties");
        return;
      }
      if (!content.startsWith("BUILD_DATE=")) {
        LOGGER.error("Invalid format of build date");
        return;
      }
      String buildDate = content.split("=")[1].trim();
      properties.put("kamehouse.build.date", buildDate);
    } catch (IOException e) {
      LOGGER.error("Error loading kamehouse build date into properties", e);
    }
  }
}
