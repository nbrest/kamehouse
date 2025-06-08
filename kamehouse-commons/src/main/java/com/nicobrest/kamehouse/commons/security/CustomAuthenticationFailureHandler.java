package com.nicobrest.kamehouse.commons.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * Custom login failure handler that redirects back to the login page.
 *
 * @author nbrest
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private static final String LOGIN_ERROR_URL = "/kame-house/login.html?error=true";
  private static final Logger log = LoggerFactory.getLogger(
      CustomAuthenticationFailureHandler.class);

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    log.debug("Login error. Sending redirect to {}", LOGIN_ERROR_URL);
    response.sendRedirect(LOGIN_ERROR_URL);
  }
}
