package com.nicobrest.kamehouse.commons.servlet;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseConflictException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.exception.KameHouseForbiddenException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.exception.KameHouseNotFoundException;
import com.nicobrest.kamehouse.commons.exception.KameHouseServerErrorException;
import com.nicobrest.kamehouse.commons.model.KameHouseApiErrorResponse;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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

  protected transient final Logger logger = LoggerFactory.getLogger(getClass());
  public static final String KAMEHOUSE_SESSION_ID = "KAMEHOUSE-SESSION-ID";
  private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
  private static final String SESSION_REPOSITORY_CLASS =
      "org.springframework.session.SessionRepository";
  private static final String UNAUTHORIZED = "Unauthorized user";
  private static final String ERROR_URL_PARAM = "Error getting url parameter ";
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
      logger.error("Error occurred processing request. Message: {}", e.getMessage());
      throw new KameHouseServerErrorException(e.getMessage(), e);
    }
  }

  /**
   * Decode URL Encoded parameter.
   */
  public String getUrlDecodedParam(HttpServletRequest request, String paramName) {
    try {
      String value = StringUtils.sanitizeInput(request.getParameter(paramName));
      if (value != null) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
      }
    } catch (UnsupportedEncodingException e) {
      throw new KameHouseBadRequestException(ERROR_URL_PARAM + paramName, e);
    }
    throw new KameHouseBadRequestException(ERROR_URL_PARAM + paramName);
  }

  /**
   * Decode Long url parameter.
   */
  public Long getLongUrlDecodedParam(HttpServletRequest request, String paramName) {
    try {
      String value = StringUtils.sanitizeInput(getUrlDecodedParam(request, paramName));
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new KameHouseBadRequestException(ERROR_URL_PARAM + paramName, e);
    }
  }

  /**
   * Set the response for kamehouse exceptions.
   */
  public void handleKameHouseException(HttpServletResponse response, KameHouseException exception) {
    int statusCode = getErrorStatusCode(exception);
    KameHouseApiErrorResponse responseBody = generateErrorResponseBody(statusCode,
        exception.getMessage());
    setResponseBody(response, responseBody.toString());
    response.setStatus(statusCode);
  }

  /**
   * Generate the response body to return on errors.
   */
  private KameHouseApiErrorResponse generateErrorResponseBody(int statusCode, String message) {
    KameHouseApiErrorResponse kameHouseApiErrorResponse = new KameHouseApiErrorResponse();
    kameHouseApiErrorResponse.setCode(statusCode);
    kameHouseApiErrorResponse.setMessage(message);
    return kameHouseApiErrorResponse;
  }

  /**
   * Get the response code for kamehouse exceptions.
   */
  private int getErrorStatusCode(KameHouseException exception) {
    if (exception instanceof KameHouseBadRequestException) {
      return HttpStatus.BAD_REQUEST.value();
    }
    if (exception instanceof KameHouseConflictException) {
      return HttpStatus.CONFLICT.value();
    }
    if (exception instanceof KameHouseForbiddenException) {
      return HttpStatus.FORBIDDEN.value();
    }
    if (exception instanceof KameHouseInvalidCommandException) {
      return HttpStatus.BAD_REQUEST.value();
    }
    if (exception instanceof KameHouseInvalidDataException) {
      return HttpStatus.BAD_REQUEST.value();
    }
    if (exception instanceof KameHouseNotFoundException) {
      return HttpStatus.NOT_FOUND.value();
    }
    if (exception instanceof KameHouseServerErrorException) {
      return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    return HttpStatus.INTERNAL_SERVER_ERROR.value();
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
    SessionRepository<Session> sessionRepository = getSessionRepository(request);
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

  protected SessionRepository<Session> getSessionRepository(HttpServletRequest request) {
    SessionRepository<Session> sessionRepository =
        (SessionRepository) request.getAttribute(SESSION_REPOSITORY_CLASS);
    if (sessionRepository == null) {
      logger.trace("Unable to find session repository in request");
      throw new KameHouseForbiddenException(UNAUTHORIZED);
    }
    return sessionRepository;
  }

  protected Session getSession(SessionRepository<Session> sessionRepository, String sessionId) {
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
