package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseApiErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Controller to globally handle exceptions thrown in the application that are not caught before
 * returning the response to the client.
 *
 * @author nbrest
 */
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

  @ExceptionHandler(
      value = {
          KameHouseBadRequestException.class,
          KameHouseInvalidCommandException.class,
          KameHouseInvalidDataException.class
      })
  protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        generateResponseBody(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
        new HttpHeaders(),
        HttpStatus.BAD_REQUEST,
        request);
  }

  @ExceptionHandler(value = {KameHouseConflictException.class})
  protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(
        ex, generateResponseBody(HttpStatus.CONFLICT.value(), ex.getMessage()),
        new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(value = {KameHouseForbiddenException.class})
  protected ResponseEntity<Object> handleForbidden(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        generateResponseBody(HttpStatus.FORBIDDEN.value(), ex.getMessage()),
        new HttpHeaders(),
        HttpStatus.FORBIDDEN,
        request);
  }

  @ExceptionHandler(value = {KameHouseNotFoundException.class, UsernameNotFoundException.class})
  protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        generateResponseBody(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
        new HttpHeaders(),
        HttpStatus.NOT_FOUND,
        request);
  }

  @ExceptionHandler(value = {KameHouseServerErrorException.class})
  protected ResponseEntity<Object> handleServerError(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(
        ex,
        generateResponseBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
    logger.error(ex.getMessage(), ex);
    return handleExceptionInternal(
        ex,
        generateResponseBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

  /**
   * Generate the response body to return on errors.
   */
  private KameHouseApiErrorResponse generateResponseBody(int code, String message) {
    KameHouseApiErrorResponse kameHouseApiErrorResponse = new KameHouseApiErrorResponse();
    kameHouseApiErrorResponse.setCode(code);
    kameHouseApiErrorResponse.setMessage(message);
    return kameHouseApiErrorResponse;
  }
}
