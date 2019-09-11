package com.nicobrest.kamehouse.admin.security;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CustomAuthenticationSuccessHandler
    extends SavedRequestAwareAuthenticationSuccessHandler {

  private static final Logger logger = LoggerFactory
      .getLogger(CustomAuthenticationSuccessHandler.class);

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {

    /*
     * If I need to do a custom redirect logic, I can read the destination
     * redirect-url from a parameter and set the targetUrlParameter defined on the
     * parent class with the method setTargetUrlParameter. I removed that logic now,
     * because for now I serve the pages that require authentication from the
     * backend, so I no longer need a custom authentication on success handler.
     */
    String redirectUrl = StringEscapeUtils.escapeHtml(request.getParameter("redirect-url"));
    if (redirectUrl != null) {
      // here I would set the setTargetUrlParameter to redirect to that page.
      logger.trace("Custom redirect url set, but not used yet: {}", redirectUrl);
    }
    super.onAuthenticationSuccess(request, response, authentication);
  }
}