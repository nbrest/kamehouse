package com.nicobrest.kamehouse.main.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom SavedRequestAwareAuthenticationSuccessHandler that redirects to the
 * specified page in the redirect-url parameter, if it's set. Otherwise executes
 * the standard behavior. I need to use this custom class and capture the
 * original url before logging in, because the latest referer is /login, so if I
 * use the referer header to redirect to it, it redirects to the login page. So
 * I capture the referer to the login page in /login.jsp and pass it here in the
 * POST request to process the login. This is because I'm redirecting to /login
 * from javascript since I moved the static code away from jsps.
 * 
 * @author nbrest
 *
 */
public class CustomAuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {

    String redirectUrl = request.getParameter("redirect-url");
    if (redirectUrl != null) {
      setTargetUrlParameter("redirect-url");
    }
    super.onAuthenticationSuccess(request, response, authentication);
    return;
  }
}