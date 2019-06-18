package com.nicobrest.kamehouse.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * Utility class to manage the application properties.
 * 
 * @author nbrest
 *
 */
public class PropertiesUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
  private static final boolean IS_WINDOWS_HOST = setIsWindowsHost();
  private static final Properties mediaVideoProperties = new Properties();
  
  static { 
    try {
      Resource resource = new ClassPathResource("/media.video.properties");
      Properties properties = PropertiesLoaderUtils.loadProperties(resource);
      mediaVideoProperties.putAll(properties);
    } catch (IOException e) {
      logger.error("Exception loading properties file. Message: " + e.getMessage());
      e.printStackTrace();
    }
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
   * Gets the specified property from the media.video application properties.
   */
  public static String getMediaVideoProperty(String propertyName) {
    return mediaVideoProperties.getProperty(propertyName);
  }
}
