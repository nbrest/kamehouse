package com.nicobrest.kamehouse.commons.servlet;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

/**
 * Abstract class to group common servlet functionality.
 *
 * @author nbrest
 */
public abstract class AbstractKameHouseServlet extends HttpServlet {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  public static final String KAMEHOUSE_SESSION_ID = "KAMEHOUSE-SESSION-ID";
  private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
  private static final String SESSION_REPOSITORY_CLASS =
      "org.springframework.session.SessionRepository";
  private static final String UNAUTHORIZED = "Unauthorized user";
  private static final long serialVersionUID = 1L;

  /**
   * Authenticate the current request with the session and the authorized roles for this request.
   */
  public void authorize(HttpServletRequest request, List<String> authorizedRoles) {
    String sessionId = getSessionId(request);
    List<String> currentRoles = getCurrentUserRoles(request, sessionId);
    boolean isAuthorized = false;
    for (String role : currentRoles) {
      if (authorizedRoles.contains(role)) {
        isAuthorized = true;
      }
    }
    if (!isAuthorized) {
      logger.trace("Current user is not authorized to access this resource");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    logger.trace("User is authorized to execute this request");
  }

  /**
   * Write the response body.
   */
  public void setResponseBody(HttpServletResponse response, String responseBody)
      throws KameHouseServerErrorException {
    try {
      response.getWriter().write(responseBody);
      response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    } catch (IOException e) {
      logger.error("Error occurred processing request.", e);
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Decode URL Encoded parameter.
   */
  public String getUrlDecodedParam(HttpServletRequest request, String paramName) {
    try {
      String value = request.getParameter(paramName);
      if (value != null) {
        return URLDecoder.decode(request.getParameter(paramName), StandardCharsets.UTF_8.name());
      }
    } catch (UnsupportedEncodingException e) {
      throw new KameHouseBadRequestException("Error getting url parameter " + paramName, e);
    }
    throw new KameHouseBadRequestException("Error getting url parameter " + paramName);
  }

  /**
   * Decode Long url parameter.
   */
  public Long getLongUrlDecodedParam(HttpServletRequest request, String paramName) {
    try {
      String value = getUrlDecodedParam(request, paramName);
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new KameHouseBadRequestException("Error getting url parameter " + paramName, e);
    }
  }

  /**
   * Set the response for kamehouse exceptions.
   */
  public void handleKameHouseException(HttpServletResponse response, KameHouseException exception) {
    KameHouseGenericResponse responseBody = generateErrorResponseBody(exception.getMessage());
    setResponseBody(response, responseBody.toString());
    setErrorStatusCode(response, exception);
  }

  /**
   * Generate the response body to return on errors.
   */
  private KameHouseGenericResponse generateErrorResponseBody(String message) {
    KameHouseGenericResponse kameHouseGenericResponse = new KameHouseGenericResponse();
    kameHouseGenericResponse.setMessage(message);
    return kameHouseGenericResponse;
  }

  /**
   * Set the response code for kamehouse exceptions.
   */
  private void setErrorStatusCode(HttpServletResponse response, KameHouseException exception) {
    if (exception instanceof KameHouseBadRequestException) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    if (exception instanceof KameHouseConflictException) {
      response.setStatus(HttpStatus.CONFLICT.value());
    }
    if (exception instanceof KameHouseForbiddenException) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
    }
    if (exception instanceof KameHouseInvalidCommandException) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    if (exception instanceof KameHouseInvalidDataException) {
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    if (exception instanceof KameHouseNotFoundException) {
      response.setStatus(HttpStatus.NOT_FOUND.value());
    }
    if (exception instanceof KameHouseServerErrorException) {
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  private String getSessionId(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null || cookies.length == 0) {
      logger.trace("No cookies in request to find the sessionId");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    String sessionId = null;
    for (Cookie cookie : cookies) {
      if (KAMEHOUSE_SESSION_ID.equals(cookie.getName())) {
        try {
          byte[] decodedSessionId = Base64.getDecoder().decode(cookie.getValue());
          sessionId = new String(decodedSessionId, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
          logger.trace("Unable to parse sessionId cookie");
          throw new KameHouseForbiddenException(UNAUTHORIZED);
        }
      }
    }
    if (StringUtils.isEmpty(sessionId)) {
      logger.trace("Session id is empty");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    return sessionId;
  }

  private List<String> getCurrentUserRoles(HttpServletRequest request, String sessionId) {
    SessionRepository sessionRepository = getSessionRepository(request);
    Session session = getSession(sessionRepository, sessionId);
    Instant lastAccess = session.getLastAccessedTime();
    Duration maxInactive = session.getMaxInactiveInterval();
    Instant expiryTime = lastAccess.plus(maxInactive);
    if (expiryTime.isBefore(Instant.now())) {
      logger.trace("Session expired");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    SecurityContext securityContext = getSecurityContext(session);
    Authentication authentication = getAuthentication(securityContext);

    if (authentication.getAuthorities() == null || authentication.getAuthorities().isEmpty()) {
      logger.trace("No authorities (roles) found for current authentication (user)");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    List<String> currentUserRoles = new ArrayList<>();
    for (GrantedAuthority authority : authentication.getAuthorities()) {
      currentUserRoles.add(authority.getAuthority());
    }
    return currentUserRoles;
  }

  protected SessionRepository getSessionRepository(HttpServletRequest request) {
    SessionRepository sessionRepository =
        (SessionRepository) request.getAttribute(SESSION_REPOSITORY_CLASS);
    if (sessionRepository == null) {
      logger.trace("Unable to find session repository in request");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    return sessionRepository;
  }

  protected Session getSession(SessionRepository sessionRepository, String sessionId) {
    Session session = sessionRepository.findById(sessionId);
    if (session == null) {
      logger.trace("Unable to find session in repository");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    return session;
  }

  protected SecurityContext getSecurityContext(Session session) {
    SecurityContext securityContext = session.getAttribute(SPRING_SECURITY_CONTEXT);
    if (securityContext == null) {
      logger.trace("Unable to find security context in session");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    return securityContext;
  }

  protected Authentication getAuthentication(SecurityContext securityContext) {
    Authentication authentication = securityContext.getAuthentication();
    if (authentication == null) {
      logger.trace("Unable to find authentication (user) in security context");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    return authentication;
  }
}
