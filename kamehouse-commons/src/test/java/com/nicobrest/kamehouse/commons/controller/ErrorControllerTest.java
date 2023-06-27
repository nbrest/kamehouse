package com.nicobrest.kamehouse.commons.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.KameHouseApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Unit tests for the ErrorController.
 *
 * @author nbrest
 */
public class ErrorControllerTest {

  private ErrorController errorController = new ErrorController();
  @Mock
  private MockHttpServletRequest request = new MockHttpServletRequest();

  @BeforeEach
  public void beforeTest() {
    MockitoAnnotations.openMocks(this);
    when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(new Integer("404"));
    when(request.getAttribute("javax.servlet.error.message")).thenReturn("mock message");
  }

  @Test
  public void handleErrorsTest() {
    ResponseEntity<KameHouseApiErrorResponse> responseEntity = errorController.errors(request);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    KameHouseApiErrorResponse responseBody = responseEntity.getBody();
    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.getCode());
    assertEquals("mock message", responseBody.getMessage());
  }

  @Test
  public void handleErrors400Test() {
    when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(new Integer("400"));
    when(request.getAttribute("javax.servlet.error.message")).thenReturn("");
    ResponseEntity<KameHouseApiErrorResponse> responseEntity = errorController.errors(request);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    KameHouseApiErrorResponse responseBody = responseEntity.getBody();
    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.getCode());
    assertEquals("I have a bad feeling about this", responseBody.getMessage());
  }

  @Test
  public void handleErrors401Test() {
    when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(new Integer("401"));
    when(request.getAttribute("javax.servlet.error.message")).thenReturn("");
    ResponseEntity<KameHouseApiErrorResponse> responseEntity = errorController.errors(request);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    KameHouseApiErrorResponse responseBody = responseEntity.getBody();
    assertEquals(HttpStatus.UNAUTHORIZED.value(), responseBody.getCode());
    assertEquals("You underestimate my power", responseBody.getMessage());
  }

  @Test
  public void handleErrors403Test() {
    when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(new Integer("403"));
    when(request.getAttribute("javax.servlet.error.message")).thenReturn("");
    ResponseEntity<KameHouseApiErrorResponse> responseEntity = errorController.errors(request);
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    KameHouseApiErrorResponse responseBody = responseEntity.getBody();
    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.getCode());
    assertEquals("You don't know the power of the dark side", responseBody.getMessage());
  }
}
