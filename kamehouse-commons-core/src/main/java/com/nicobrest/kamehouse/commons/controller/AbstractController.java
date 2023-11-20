package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.model.PasswordEntity;
import com.nicobrest.kamehouse.commons.utils.PasswordUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.lang.reflect.Field;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Superclass to all controllers that groups common functionality to all of them.
 *
 * @author nbrest
 */
public abstract class AbstractController {

  protected static final ResponseEntity<Void> EMPTY_SUCCESS_RESPONSE =
      new ResponseEntity<>(HttpStatus.OK);
  protected static final Logger STATIC_LOGGER = LoggerFactory.getLogger(AbstractController.class);
  // I define the non-static logger here to avoid having to define it in every controller
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private static final String RESPONSE_ENTITY = "Response {}";
  private static final String ENTITY_NOT_FOUND = "Empty response. Entity not found.";

  /**
   * Generates a standard response entity for get requests.
   */
  protected static <T> ResponseEntity<T> generateGetResponseEntity(T entity, boolean logResponse) {
    STATIC_LOGGER.debug("Building GET response");
    return generateStandardResponseEntity(entity, logResponse);
  }

  /**
   * Generates a standard response entity for get requests.
   */
  protected static <T> ResponseEntity<T> generateGetResponseEntity(T entity) {
    STATIC_LOGGER.debug("Building GET response");
    return generateStandardResponseEntity(entity);
  }

  /**
   * Generates a standard response entity for delete requests.
   */
  protected static <T> ResponseEntity<T> generateDeleteResponseEntity(T entity) {
    STATIC_LOGGER.debug("Building DELETE response");
    return generateStandardResponseEntity(entity);
  }

  /**
   * Generates a standard EMPTY response entity for put requests.
   */
  protected static ResponseEntity<Void> generatePutResponseEntity() {
    STATIC_LOGGER.debug("PUT operation executed successfully");
    return EMPTY_SUCCESS_RESPONSE;
  }

  /**
   * Generates a standard response entity for put requests that expect a response body.
   */
  protected static <T> ResponseEntity<T> generatePutResponseEntity(T entity) {
    STATIC_LOGGER.debug("Building PUT response");
    ResponseEntity<T> responseEntity = null;
    if (entity != null) {
      STATIC_LOGGER.trace(RESPONSE_ENTITY, entity);
      responseEntity = new ResponseEntity<>(entity, HttpStatus.OK);
    } else {
      STATIC_LOGGER.warn(ENTITY_NOT_FOUND);
      responseEntity = ResponseEntity.notFound().build();
    }
    return responseEntity;
  }

  /**
   * Generates a standard response entity for post requests.
   */
  protected static <T> ResponseEntity<T> generatePostResponseEntity(T entity, boolean logResponse) {
    STATIC_LOGGER.debug("Building POST response");
    ResponseEntity<T> responseEntity = null;
    if (entity != null) {
      if (logResponse) {
        STATIC_LOGGER.trace(RESPONSE_ENTITY, entity);
      }
      responseEntity = new ResponseEntity<>(entity, HttpStatus.CREATED);
    } else {
      if (logResponse) {
        STATIC_LOGGER.warn(ENTITY_NOT_FOUND);
      }
      responseEntity = ResponseEntity.notFound().build();
    }
    return responseEntity;
  }

  /**
   * Generates a standard response entity for post requests logging the response.
   */
  protected static <T> ResponseEntity<T> generatePostResponseEntity(T entity) {
    return generatePostResponseEntity(entity, true);
  }

  /**
   * Removes the password from the entity of the response body.
   */
  protected static <T> ResponseEntity<T> generatePasswordLessResponseEntity(
      ResponseEntity<T> responseEntity) {
    T responseBody = responseEntity.getBody();
    if (responseBody instanceof PasswordEntity) {
      PasswordUtils.unsetPassword((PasswordEntity) responseBody);
    }
    if (responseBody instanceof List) {
      PasswordUtils.unsetPassword((List<T>) responseBody);
    }
    return responseEntity;
  }

  /**
   * Checks that the id in the path of the url matches the id of the request body. This is to avoid
   * updating a wrong entity if they don't match.
   */
  protected static void validatePathAndRequestBodyIds(Long pathId, Long requestBodyId) {
    if (pathId == null) {
      String errorMessage = "Id is required in the path.";
      STATIC_LOGGER.error(errorMessage);
      throw new KameHouseBadRequestException(errorMessage);
    }
    if (!pathId.equals(requestBodyId)) {
      String errorMessage =
          "Id in path " + pathId + " doesn't match id in request body " + requestBodyId;
      STATIC_LOGGER.error(errorMessage);
      throw new KameHouseBadRequestException(errorMessage);
    }
  }

  /**
   * Sanitize String fields in input entity.
   */
  protected static <T> void sanitizeEntity(T entity) {
    STATIC_LOGGER.trace("Sanitizing entity");
    if (entity == null) {
      return;
    }
    Field[] fields = entity.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (String.class.equals(field.getType())) {
        try {
          boolean currentAccessibility = field.trySetAccessible();
          field.setAccessible(true);
          field.set(entity, StringUtils.sanitizeInput((String) field.get(entity)));
          field.setAccessible(currentAccessibility);
        } catch (IllegalAccessException e) {
          STATIC_LOGGER.trace("Error sanitizing. Field: {}", field.getName());
        }
      }
      if (hasSubFields(field)) {
        try {
          field.setAccessible(true);
          Object fieldValue = field.get(entity);
          sanitizeEntity(fieldValue);
        } catch (IllegalAccessException e) {
          STATIC_LOGGER.trace("Error accessing object field to sanitize. Field: {}",
              field.getName());
        }
      }
    }
  }

  /**
   * Check if the field has subfields.
   */
  private static boolean hasSubFields(Field field) {
    if (!field.getType().getCanonicalName().contains("com.nicobrest.kamehouse")) {
      return false;
    }
    if (field.getType().isEnum()) {
      return false;
    }
    Field[] subfields = field.getType().getDeclaredFields();
    if (subfields != null && subfields.length > 0) {
      return true;
    }
    return false;
  }

  /**
   * Generates a standard response entity with the entity parameter as a body and 200 return code
   * and a 404 with empty body if the entity is null.
   */
  private static <T> ResponseEntity<T> generateStandardResponseEntity(T entity,
      boolean logResponse) {
    ResponseEntity<T> responseEntity = null;
    if (entity != null) {
      if (logResponse) {
        STATIC_LOGGER.trace(RESPONSE_ENTITY, entity);
      }
      responseEntity = ResponseEntity.ok(entity);
    } else {
      if (logResponse) {
        STATIC_LOGGER.warn(ENTITY_NOT_FOUND);
      }
      responseEntity = ResponseEntity.notFound().build();
    }
    return responseEntity;
  }

  /**
   * Generate a standard response entity logging the response.
   */
  private static <T> ResponseEntity<T> generateStandardResponseEntity(T entity) {
    return generateStandardResponseEntity(entity, true);
  }
}
