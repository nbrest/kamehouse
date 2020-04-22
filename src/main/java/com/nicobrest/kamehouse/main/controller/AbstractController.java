package com.nicobrest.kamehouse.main.controller;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Superclass to all controllers that groups common functionality to all of
 * them.
 *
 * @author nbrest
 */
public abstract class AbstractController {

  protected static final ResponseEntity<Void> EMPTY_SUCCESS_RESPONSE =
      new ResponseEntity<>(HttpStatus.OK);
  protected static final Logger STATIC_LOGGER = LoggerFactory.getLogger(AbstractController.class);
  // I define the non static logger here to avoid having to define it in every controller
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Generates a standard response entity for get requests.
   */
  protected static <T> ResponseEntity<T> generateGetResponseEntity(T entity) {
    return generateStandardResponseEntity(entity);
  }

  /**
   * Generates a standard response entity for delete requests.
   */
  protected static <T> ResponseEntity<T> generateDeleteResponseEntity(T entity) {
    return generateStandardResponseEntity(entity);
  }

  /**
   * Generates a standard response entity for put requests.
   */
  protected static ResponseEntity<Void> generatePutResponseEntity() {
    STATIC_LOGGER.trace("PUT operation executed successfully");
    return EMPTY_SUCCESS_RESPONSE;
  }

  /**
   * Generates a standard response entity for post requests.
   */
  protected static <T> ResponseEntity<T> generatePostResponseEntity(T entity) {
    ResponseEntity<T> responseEntity = null;
    if (entity != null) {
      STATIC_LOGGER.trace("Response {}", entity);
      responseEntity = new ResponseEntity<>(entity, HttpStatus.CREATED);
    } else {
      STATIC_LOGGER.warn("Empty response. Entity not found.");
      responseEntity = ResponseEntity.notFound().build();
    }
    return responseEntity;
  }

  /**
   * Checks that the id in the path of the url matches the id of the request body.
   * This is to avoid updating a wrong entity if they don't match.
   */
  protected static void validatePathAndRequestBodyIds(Long pathId, Long requestBodyId) {
    if (pathId == null) {
      String errorMessage = "Id is required in the path.";
      STATIC_LOGGER.error(errorMessage);
      throw new KameHouseBadRequestException(errorMessage);
    }
    if (!pathId.equals(requestBodyId)) {
      String errorMessage = "Id in path " + pathId
          + " doesn't match id in request body " + requestBodyId;
      STATIC_LOGGER.error(errorMessage);
      throw new KameHouseBadRequestException(errorMessage);
    }
  }

  /**
   * Generates a standard response entity with the entity parameter as a body and
   * 200 return code and a 404 with empty body if the entity is null.
   */
  private static <T> ResponseEntity<T> generateStandardResponseEntity(T entity) {
    ResponseEntity<T> responseEntity = null;
    if (entity != null) {
      STATIC_LOGGER.trace("Response {}", entity);
      responseEntity = ResponseEntity.ok(entity);
    } else {
      STATIC_LOGGER.warn("Empty response. Entity not found.");
      responseEntity = ResponseEntity.notFound().build();
    }
    return responseEntity;
  }
}
