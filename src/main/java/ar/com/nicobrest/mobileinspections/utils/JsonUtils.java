package ar.com.nicobrest.mobileinspections.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 *        Utility class to process jsons.
 * 
 * @author nbrest
 */
public class JsonUtils {

  /**
   *      Converts an object to a json byte array.
   * 
   * @author nbrest
   */
  public static byte[] convertToJsonBytes(Object object) throws IOException {
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper.writeValueAsBytes(object);
  }
}
