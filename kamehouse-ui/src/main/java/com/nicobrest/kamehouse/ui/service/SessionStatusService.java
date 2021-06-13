package com.nicobrest.kamehouse.ui.service;

import com.nicobrest.kamehouse.commons.model.ApplicationUser;
import com.nicobrest.kamehouse.commons.service.ApplicationUserAuthenticationService;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.ui.model.SessionStatus;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * Service layer to get the session status.
 * 
 * @author nbrest
 *
 */
@Service
public class SessionStatusService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  @Autowired
  private ApplicationUserAuthenticationService applicationUserAuthenticationService;
  
  public void setApplicationUserAuthenticationService(ApplicationUserAuthenticationService
                                            applicationUserAuthenticationService) {
    this.applicationUserAuthenticationService = applicationUserAuthenticationService;
  }
  
  public ApplicationUserAuthenticationService getApplicationUserAuthenticationService() {
    return applicationUserAuthenticationService;
  }
  
  /**
   * Returns the current session's status.
   */
  public SessionStatus get(HttpSession session) {
    logger.trace("getting the user's session status");
    Authentication authentication = getAuthentication();
    String username = authentication.getName();
    SessionStatus sessionStatus = new SessionStatus();
    sessionStatus.setUsername(StringEscapeUtils.escapeHtml(username));
    sessionStatus.setServer(PropertiesUtils.getHostname());
    try {
      ApplicationUser applicationUser =
          applicationUserAuthenticationService.loadUserByUsername(username);
      sessionStatus.setFirstName(applicationUser.getFirstName());
      sessionStatus.setLastName(applicationUser.getLastName()); 
    } catch (UsernameNotFoundException e) {
      logger.warn(e.getMessage());
    }
    if (session != null) {
      sessionStatus.setSessionId(session.getId());
    }
    logger.trace("get session response {}", sessionStatus);
    return sessionStatus;
  }
  
  /**
   * Gets the Authentication object from the spring security context.
   */
  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
