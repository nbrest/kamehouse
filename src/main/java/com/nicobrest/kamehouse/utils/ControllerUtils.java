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
}
