package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.KameHouseApiErrorResponse;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to resolve api errors.
 *
 * @author nbrest
 */
@Controller
public class ErrorController extends AbstractController {

  /**
   * Handle api errors. This mapping should be used in the web.xml of all api based modules.
   */
  @RequestMapping(value = "errors")
  @ResponseBody
  public ResponseEntity<KameHouseApiErrorResponse> errors(HttpServletRequest request) {
    int statusCode = getStatusCode(request);
    String message = getErrorMessage(request, statusCode);
    KameHouseApiErrorResponse kameHouseApiErrorResponse = new KameHouseApiErrorResponse();
    kameHouseApiErrorResponse.setCode(statusCode);
    kameHouseApiErrorResponse.setMessage(message);
    return new ResponseEntity<>(kameHouseApiErrorResponse, HttpStatus.valueOf(statusCode));
  }

  /**
   * Get the status code.
   */
  private int getStatusCode(HttpServletRequest httpRequest) {
    return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
  }

  /**
   * Get the error message to return in the http response.
   */
  private String getErrorMessage(HttpServletRequest request, int statusCode) {
    String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
    if (!StringUtils.isEmpty(errorMessage)) {
      return errorMessage;
    }
    switch (statusCode) {
      case 400:
        return "I have a bad feeling about this";
      case 401:
        return "You underestimate my power";
      case 403:
        return "You don't know the power of the dark side";
      case 404:
        return "These aren't the droids you are looking for";
      case 405:
        return "Do or do not. There is no try";
      case 409:
        return "I can feel the conflict within you";
      case 500:
        return "Everything is going as planned";
      case 502:
        return "A powerful Sith you will become";
      case 503:
        return "Search your feelings. You know it to be true";
      default:
        return "Unexpected error executing the request. Try again later";
    }
  }
}
