package com.nicobrest.kamehouse.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
  private static final String COMMONS_POM_PROPERTIES =
      "/META-INF/maven/com.nicobrest/kamehouse-commons-core/pom.properties";

  static {
    loadAllPropertiesFiles();
    loadBuildVersionAndDate();
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
      return System.getenv("COMPUTERNAME").toLowerCase(Locale.getDefault());
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
   * Loads all properties files.
   */
  private static void loadAllPropertiesFiles() {
    loadPropertiesFile("commons.properties");
    loadPropertiesFile("kamehouse.properties");
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
   * Loads the build version and date.
   */
  private static void loadBuildVersionAndDate() {
    try {
      InputStream pomPropertiesInputStream =
          PropertiesUtils.class.getResourceAsStream(COMMONS_POM_PROPERTIES);
      List<String> pomProperties = null;
      if (pomPropertiesInputStream != null) {
        pomProperties = IOUtils.readLines(pomPropertiesInputStream, StandardCharsets.UTF_8.name());
      }
      if (pomProperties == null) {
        LOGGER.error("Error loading kamehouse build version and date into properties");
        return;
      }
      for (String pomPropertiesLine : pomProperties) {
        if (pomPropertiesLine.startsWith("#Generated")) {
          continue;
        }
        if (pomPropertiesLine.startsWith("#")) {
          properties.put("kamehouse.build.date", pomPropertiesLine.substring(1));
        }
        if (pomPropertiesLine.startsWith("version=")) {
          properties.put(
              "kamehouse.build.version",
              pomPropertiesLine.replace("version=", "").replace("-KAMEHOUSE-SNAPSHOT", ""));
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error loading kamehouse build version and date into properties", e);
    }
  }
}
