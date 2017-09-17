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
 * Filter class to add the logged in username to each request.
 * 
 * @author nbrest
 *
 */
public class AddSecurityContextFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(AddSecurityContextFilter.class);
  
  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  //TODO: Add unit test
  /**
   * Add logged in username to the current request.
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    logger.trace("Setting request username: " + username);
    request.setAttribute("username", username);
    chain.doFilter(request, response);
  }
}