package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Unit tests for the ExceptionHandlerController.
 *
 * @author nbrest
 */
class ExceptionHandlerControllerTest {

  private static final String MOCK_MESSAGE = "mock message";
  private ExceptionHandlerController exceptionHandlerController = new ExceptionHandlerController();
  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();
  private WebRequest webRequest = new ServletWebRequest(request, response);

  @Test
  void handleBadRequestTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleBadRequest(
            new KameHouseBadRequestException(MOCK_MESSAGE), webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertTrue(responseEntity.getBody() instanceof KameHouseApiErrorResponse);
    KameHouseApiErrorResponse responseBody = (KameHouseApiErrorResponse) responseEntity.getBody();
    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.getCode());
    assertEquals(MOCK_MESSAGE, responseBody.getMessage());
  }

  @Test
  void handleConflictTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleConflict(new KameHouseConflictException(MOCK_MESSAGE),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    assertTrue(responseEntity.getBody() instanceof KameHouseApiErrorResponse);
    KameHouseApiErrorResponse responseBody = (KameHouseApiErrorResponse) responseEntity.getBody();
    assertEquals(HttpStatus.CONFLICT.value(), responseBody.getCode());
    assertEquals(MOCK_MESSAGE, responseBody.getMessage());
  }

  @Test
  void handleForbiddenTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleForbidden(new KameHouseForbiddenException(MOCK_MESSAGE),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    assertTrue(responseEntity.getBody() instanceof KameHouseApiErrorResponse);
    KameHouseApiErrorResponse responseBody = (KameHouseApiErrorResponse) responseEntity.getBody();
    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.getCode());
    assertEquals(MOCK_MESSAGE, responseBody.getMessage());
  }

  @Test
  void handleNotFoundTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleNotFound(new KameHouseNotFoundException(MOCK_MESSAGE),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertTrue(responseEntity.getBody() instanceof KameHouseApiErrorResponse);
    KameHouseApiErrorResponse responseBody = (KameHouseApiErrorResponse) responseEntity.getBody();
    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.getCode());
    assertEquals(MOCK_MESSAGE, responseBody.getMessage());
  }

  @Test
  void handleServerErrorTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleServerError(
            new KameHouseServerErrorException(MOCK_MESSAGE), webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertTrue(responseEntity.getBody() instanceof KameHouseApiErrorResponse);
    KameHouseApiErrorResponse responseBody = (KameHouseApiErrorResponse) responseEntity.getBody();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.getCode());
    assertEquals(MOCK_MESSAGE, responseBody.getMessage());
  }

  @Test
  void handleGenericExceptionTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleGenericException(new NullPointerException(MOCK_MESSAGE),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertTrue(responseEntity.getBody() instanceof KameHouseApiErrorResponse);
    KameHouseApiErrorResponse responseBody = (KameHouseApiErrorResponse) responseEntity.getBody();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.getCode());
    assertEquals(MOCK_MESSAGE, responseBody.getMessage());
  }
}
