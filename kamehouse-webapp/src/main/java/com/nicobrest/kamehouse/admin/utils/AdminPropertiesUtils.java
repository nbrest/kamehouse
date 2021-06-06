package com.nicobrest.kamehouse.admin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to manage the application properties.
 * 
 * @author nbrest
 *
 */
public class AdminPropertiesUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminPropertiesUtils.class);

  private static final Properties adminProperties = new Properties();

  private AdminPropertiesUtils() {
    throw new IllegalStateException("Utility class");
  }

  static {
    try {
      Resource adminPropertiesResource = new ClassPathResource("/admin.properties");
      Properties adminPropertiesFromFile = PropertiesLoaderUtils
          .loadProperties(adminPropertiesResource);
      adminProperties.putAll(adminPropertiesFromFile);
    } catch (IOException e) {
      LOGGER.error("Error loading properties files.", e);
    }
  }

  /**
   * Gets the specified property from the admin application properties.
   */
  public static String getProperty(String propertyName) {
    return adminProperties.getProperty(propertyName);
  }
}
