package com.nicobrest.kamehouse.commons.web.filter.logger;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Custom request logging filter to log all incoming requests.
 */
public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {

  @Override
  protected void initFilterBean() throws ServletException {
    super.initFilterBean();
    setBeforeMessagePrefix("Request: ");
    setBeforeMessageSuffix("");
    setAfterMessagePrefix("Request: ");
    setAfterMessageSuffix("");
    setBeanName("customRequestLoggingFilter");

    setIncludeClientInfo(true);
    setIncludeQueryString(true);
    setIncludeHeaders(false);
    setIncludePayload(false);
    setMaxPayloadLength(64000);
  }

  @Override
  protected boolean shouldLog(HttpServletRequest request) {
    return logger.isTraceEnabled();
  }

  @Override
  protected void beforeRequest(HttpServletRequest request, String message) {
    // Log before if the payload is not required
    if (!isIncludePayload()) {
      logger.trace(message + getHttpMethod(request));
    }
  }

  @Override
  protected void afterRequest(HttpServletRequest request, String message) {
    // Log before if the payload is required (it doesn't log it in beforeRequest)
    if (isIncludePayload()) {
      logger.trace(message + getHttpMethod(request));
    }
  }

  /**
   * Get the http method to log it.
   */
  private String getHttpMethod(HttpServletRequest request) {
    return ";method=" + request.getMethod();
  }
}