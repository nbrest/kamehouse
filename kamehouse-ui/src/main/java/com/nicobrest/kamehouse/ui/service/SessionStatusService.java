package com.nicobrest.kamehouse.ui.service;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.service.KameHouseUserAuthenticationService;
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
  private KameHouseUserAuthenticationService kameHouseUserAuthenticationService;
  
  public void setKameHouseUserAuthenticationService(KameHouseUserAuthenticationService
                                                        kameHouseUserAuthenticationService) {
    this.kameHouseUserAuthenticationService = kameHouseUserAuthenticationService;
  }
  
  public KameHouseUserAuthenticationService getKameHouseUserAuthenticationService() {
    return kameHouseUserAuthenticationService;
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
      KameHouseUser kameHouseUser =
          kameHouseUserAuthenticationService.loadUserByUsername(username);
      sessionStatus.setFirstName(kameHouseUser.getFirstName());
      sessionStatus.setLastName(kameHouseUser.getLastName());
    } catch (UsernameNotFoundException e) {
      logger.warn(e.getMessage());
    }
    if (session != null) {
      sessionStatus.setSessionId(session.getId());
    }
    sessionStatus.setBuildVersion(PropertiesUtils.getProperty("kamehouse.build.version"));
    sessionStatus.setBuildDate(PropertiesUtils.getProperty("kamehouse.build.date"));
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
