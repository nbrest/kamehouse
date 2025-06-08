package com.nicobrest.kamehouse.commons.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.ForwardLogoutSuccessHandler;

/**
 * Custom logout success handler that redirects back to the login page.
 *
 * @author nbrest
 */
public class CustomLogoutSuccessHandler extends ForwardLogoutSuccessHandler {

  private static final String LOGOUT_SUCCESS = "/kame-house/login.html?logout=true";
  private static final Logger log = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

  public CustomLogoutSuccessHandler() {
    super(LOGOUT_SUCCESS);
  }

  public CustomLogoutSuccessHandler(String targetUrl) {
    super(targetUrl);
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    log.debug("Logout success. Sending redirect to {}", LOGOUT_SUCCESS);
    response.sendRedirect(LOGOUT_SUCCESS);
  }
}
