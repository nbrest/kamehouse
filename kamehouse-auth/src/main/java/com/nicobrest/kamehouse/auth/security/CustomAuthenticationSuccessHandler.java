package com.nicobrest.kamehouse.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Custom success login handler to redirect to redirect-url parameter when set or to the default
 * login success url.
 *
 * @author nbrest
 */
public class CustomAuthenticationSuccessHandler
    extends SavedRequestAwareAuthenticationSuccessHandler {

  private static final String DEFAULT_LOGIN_SUCCESS_URL = "/kame-house/";
  private static final Logger log =
      LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    log.debug("Login success. Sending redirect to {}", DEFAULT_LOGIN_SUCCESS_URL);
    response.sendRedirect(DEFAULT_LOGIN_SUCCESS_URL);
  }
}
