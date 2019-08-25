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

  private static final ResponseEntity<?> NOT_FOUND_RESPONSE_ENTITY = ResponseEntity.notFound()
      .build();

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
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }

  /**
   * Generates a standard response entity for get requests with the Object
   * parameter as a body and 200 return code and a 404 with empty body if the
   * Object is null.
   */
  public static ResponseEntity<?> generateGetStandardResponseEntity(Object object) {
    ResponseEntity<?> responseEntity = null;
    if (object != null) {
      responseEntity = ResponseEntity.ok(object);
    } else {
      responseEntity = NOT_FOUND_RESPONSE_ENTITY;
    }
    return responseEntity;
  }
}
