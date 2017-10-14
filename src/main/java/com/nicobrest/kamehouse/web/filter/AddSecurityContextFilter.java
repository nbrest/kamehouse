package com.nicobrest.kamehouse.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter class to add the logged in username to each request.<br>
 * Deprecated. Functionality replaced by
 * {@link com.nicobrest.kamehouse.controller.SessionStatusController}.<br>
 * 
 * @author nbrest
 *
 */
@Deprecated
public class AddSecurityContextFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(AddSecurityContextFilter.class);

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    Authentication authentication = getAuthentication();
    String username = authentication.getName();
    logger.trace("Setting request username: " + username);
    request.setAttribute("username", username);
    chain.doFilter(request, response);
  }
  
  /**
   * Get the Authentication object from the spring security context.
   */
  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}