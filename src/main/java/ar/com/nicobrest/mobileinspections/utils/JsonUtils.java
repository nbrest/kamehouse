package ar.com.nicobrest.mobileinspections.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 *        Utility class to process jsons
 * 
 * @since v0.03
 * @author nbrest
 */
public class JsonUtils {

  /**
   *      Converts an object to a json byte array
   * 
   * @since v0.03
   * @author nbrest
   * @param object Object to convert to json
   * @return byte[]
   * @throws IOException Exception while processing Jsons
   */
  public static byte[] convertToJsonBytes(Object object) throws IOException {
    
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    return mapper.writeValueAsBytes(object);
  }
}
