package com.nicobrest.kamehouse.commons.servlet;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * Abstract class to group common servlet functionality.
 *
 * @author nbrest
 */
public abstract class AbstractKameHouseServlet extends HttpServlet {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  private static final long serialVersionUID = 1L;

  /**
   * Write the response body.
   */
  public void setResponseBody(HttpServletResponse response, String responseBody)
      throws KameHouseServerErrorException {
    try {
      response.getWriter().write(responseBody);
      response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    } catch (IOException e) {
      logger.error("Error occurred processing request.", e);
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Decode URL Encoded parameter.
   */
  public String getUrlDecodedParam(HttpServletRequest request, String paramName) {
    try {
      String value = request.getParameter(paramName);
      if (value != null) {
        return URLDecoder.decode(request.getParameter(paramName), StandardCharsets.UTF_8.name());
      }
    } catch (UnsupportedEncodingException e) {
      throw new KameHouseBadRequestException("Error getting url parameter " + paramName, e);
    }
    throw new KameHouseBadRequestException("Error getting url parameter " + paramName);
  }

  /**
   * Decode Long url parameter.
   */
  public Long getLongUrlDecodedParam(HttpServletRequest request, String paramName) {
    try {
      String value = getUrlDecodedParam(request, paramName);
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new KameHouseBadRequestException("Error getting url parameter " + paramName, e);
    }
  }

  /**
   * Set the response for kamehouse exceptions.
   */
  public void handleKameHouseException(HttpServletResponse response, KameHouseException exception) {
    KameHouseGenericResponse responseBody = generateErrorResponseBody(exception.getMessage());
    setResponseBody(response, responseBody.toString());
    setErrorStatusCode(response, exception);
  }

  /**
   * Generate the response body to return on errors.
   */
  private KameHouseGenericResponse generateErrorResponseBody(String message) {
    KameHouseGenericResponse kameHouseGenericResponse = new KameHouseGenericResponse();
    kameHouseGenericResponse.setMessage(message);
    return kameHouseGenericResponse;
  }

  /**
   * Set the response code for kamehouse exceptions.
   */
  private void setErrorStatusCode(HttpServletResponse response, KameHouseException exception) {
    if (exception instanceof KameHouseBadRequestException) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    if (exception instanceof KameHouseConflictException) {
      response.setStatus(HttpStatus.CONFLICT.value());
    }
    if (exception instanceof KameHouseForbiddenException) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
    }
    if (exception instanceof KameHouseInvalidCommandException) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    if (exception instanceof KameHouseInvalidDataException) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    if (exception instanceof KameHouseNotFoundException) {
      response.setStatus(HttpStatus.NOT_FOUND.value());
    }
    if (exception instanceof KameHouseServerErrorException) {
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }
}
