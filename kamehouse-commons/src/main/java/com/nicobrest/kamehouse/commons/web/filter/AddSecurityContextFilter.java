package com.nicobrest.kamehouse.commons.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Filter class to add the logged in username to each request. Functionality replaced by
 * {@link com.nicobrest.kamehouse.ui.controller.SessionStatusController}.
 *
 * @author nbrest
 */
@Deprecated(since = "A long long time ago, in a galaxy far far away")
public class AddSecurityContextFilter implements Filter {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void destroy() {
    // No resources need to be cleared after the execution of the filter.
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // No initialization is required to execute this filter.
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    Authentication authentication = getAuthentication();
    String username = authentication.getName();
    logger.trace("Setting request username: {}", username);
    request.setAttribute("username", username);
    chain.doFilter(request, response);
  }

  /**
   * Gets the Authentication object from the spring security context.
   */
  protected Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
