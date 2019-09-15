package com.nicobrest.kamehouse.utils;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
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
   * Generates a standard response entity for get requests with the entity
   * parameter as a body and 200 return code and a 404 with empty body if the
   * entity is null.
   */
  public static <T> ResponseEntity<T> generateGetStandardResponseEntity(T entity) {
    ResponseEntity<T> responseEntity = null;
    if (entity != null) {
      responseEntity = ResponseEntity.ok(entity);
    } else {
      responseEntity = ResponseEntity.notFound().build();
    }
    return responseEntity;
  }

  /**
   * Check that the id in the path of the url matches the id of the request
   * body. This is to avoid updating a wrong entity if they don't match.
   */
  public static void validatePathAndRequestBodyIds(Long pathId, Long requestBodyId) {
    if (pathId == null) {
      throw new KameHouseBadRequestException("Invalid id in path.");
    }
    if (!pathId.equals(requestBodyId)) {
      throw new KameHouseBadRequestException(
          "Id in path variable doesn't match id in request body.");
    }
  }
}
