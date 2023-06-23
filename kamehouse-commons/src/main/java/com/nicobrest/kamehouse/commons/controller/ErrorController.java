package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.utils.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller to resolve error views.
 *
 * @author nbrest
 */
@Controller
public class ErrorController extends AbstractController {

  /**
   * Handle error views mappings.
   */
  @RequestMapping(value = "errors")
  public ModelAndView error(HttpServletRequest request) {
    int statusCode = getStatusCode(request);
    ModelAndView modelAndView;
    if (!isApiRequest(request) && isGetRequest(request) && isNotFoundRequest(statusCode)) {
      modelAndView = new ModelAndView("/errors/404");
    } else {
      modelAndView = new ModelAndView("/errors/kamehouse-api-error-response");
    }
    String message = getErrorMessage(request, statusCode);
    modelAndView.addObject("code", statusCode);
    modelAndView.addObject("message", message);
    return modelAndView;
  }

  /**
   * Get the status code.
   */
  private int getStatusCode(HttpServletRequest httpRequest) {
    return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
  }

  /**
   * Check if the request comes from an API call.
   */
  private boolean isApiRequest(HttpServletRequest request) {
    String requestUrl = (String) request.getAttribute("javax.servlet.error.request_uri");
    return requestUrl != null
        && (requestUrl.contains("/api/") || !requestUrl.startsWith("/kame-house/"));
  }

  /**
   * Returns true for 404 not found requests.
   */
  private boolean isNotFoundRequest(int statusCode) {
    return statusCode == HttpStatus.SC_NOT_FOUND;
  }

  /**
   * True if the http request is GET.
   */
  private boolean isGetRequest(HttpServletRequest request) {
    return HttpMethod.GET.name().equals(request.getMethod());
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
        return "Error executing request";
    }
  }
}
