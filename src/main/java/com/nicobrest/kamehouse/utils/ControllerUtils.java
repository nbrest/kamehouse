package com.nicobrest.kamehouse.utils;

import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Utility class for common methods in the controller layer.
 * 
 * @author nbrest
 *
 */
public class ControllerUtils {
  
  private ControllerUtils() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Generates a response entity for a list of SystemCommandOutputs.
   */
  public static ResponseEntity<List<SystemCommandOutput>> generateResponseEntity(
      List<SystemCommandOutput> commandOutputs) {
    HttpStatus httpStatus = HttpStatus.OK; 
    for (SystemCommandOutput commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }         
    return new ResponseEntity<>(commandOutputs, httpStatus);
  }

  /**
   * Generates a standard response entity for get requests with the Object
   * parameter as a body and 200 return code and a 404 with empty body if the
   * Object is null.
   */
  public static <T>  ResponseEntity<T> generateGetStandardResponseEntity(T object) {
    ResponseEntity<T> responseEntity = null;
    if (object != null) {
      responseEntity = ResponseEntity.ok(object);
    } else {
      responseEntity = ResponseEntity.notFound().build();
    }
    return responseEntity;
  }
}
