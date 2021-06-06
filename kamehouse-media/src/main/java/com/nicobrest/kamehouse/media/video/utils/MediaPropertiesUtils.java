package com.nicobrest.kamehouse.media.video.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to manage the media properties.
 * 
 * @author nbrest
 *
 */
public class MediaPropertiesUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(MediaPropertiesUtils.class);

  private static final Properties mediaVideoProperties = new Properties();

  private MediaPropertiesUtils() {
    throw new IllegalStateException("Utility class");
  }

  static {
    try {
      Resource mediaVideoPropertiesResource = new ClassPathResource("/media.video.properties");
      Properties mediaVideoPropertiesFromFile = PropertiesLoaderUtils
          .loadProperties(mediaVideoPropertiesResource);
      mediaVideoProperties.putAll(mediaVideoPropertiesFromFile);
    } catch (IOException e) {
      LOGGER.error("Error loading properties files.", e);
    }
  }

  /**
   * Gets the specified property from the media set of properties.
   */
  public static String getProperty(String propertyName) {
    return mediaVideoProperties.getProperty(propertyName);
  }
}
