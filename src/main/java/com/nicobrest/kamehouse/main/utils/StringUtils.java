package com.nicobrest.kamehouse.main.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Utility class to manipulate strings.
 *
 * @author nbrest
 */
public class StringUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

  private StringUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Removes potentially dangerous characters from external input.
   * This method will need to be updated constantly.
   */
  public static String sanitizeInput(String input) {
    return input.replaceAll("[\n|\r|\t]", "_");
  }
}
