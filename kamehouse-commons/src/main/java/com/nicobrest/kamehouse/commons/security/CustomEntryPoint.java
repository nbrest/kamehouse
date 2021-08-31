package com.nicobrest.kamehouse.commons.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Custom entry point handler for unauthorized requests. The default behavior is to redirect to
 * login page, but updating it so that it returns 401 Unauthorized for API requests.
 */
public class CustomEntryPoint extends LoginUrlAuthenticationEntryPoint {

  public CustomEntryPoint(String loginPageUrl) {
    super(loginPageUrl);
  }

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    if (request.getRequestURI().contains("/api/")) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    } else {
      super.commence(request, response, exception);
    }
  }
}
