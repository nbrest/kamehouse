package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
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
public class ExceptionHandlerControllerTest {

  private ExceptionHandlerController exceptionHandlerController = new ExceptionHandlerController();
  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();
  private WebRequest webRequest = new ServletWebRequest(request, response);

  @Test
  public void handleBadRequestTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleBadRequest(new KameHouseBadRequestException(""),
        webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void handleConflictTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleConflict(new KameHouseConflictException(""),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
  }

  @Test
  public void handleForbiddenTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleForbidden(new KameHouseForbiddenException(""),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
  }

  @Test
  public void handleNotFoundTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleNotFound(new KameHouseNotFoundException(""),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  public void handleServerErrorTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleServerError(new KameHouseServerErrorException(""),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  public void handleGenericExceptionTest() {
    ResponseEntity<Object> responseEntity =
        exceptionHandlerController.handleGenericException(new NullPointerException(""),
            webRequest);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }
}
