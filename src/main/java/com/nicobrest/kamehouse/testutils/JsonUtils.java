package com.nicobrest.kamehouse.testutils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Utility class to process jsons used in the test classes. This code is not
 * necessary in the application. Only in the test classes. Had to move it to the
 * main package because eclipse randomly stops finding it in /src/test/java
 * 
 * @author nbrest
 */
public class JsonUtils {

  private JsonUtils() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Converts an object to a json byte array.
   */
  public static byte[] convertToJsonBytes(Object object) throws IOException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper.writeValueAsBytes(object);
  }
}
