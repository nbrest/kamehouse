package com.nicobrest.kamehouse.ui.service;

import com.nicobrest.kamehouse.commons.model.KameHouseUser;
import com.nicobrest.kamehouse.commons.service.KameHouseUserAuthenticationService;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.ui.model.SessionStatus;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service layer to get the session status.
 *
 * @author nbrest
 */
@Service
public class SessionStatusService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private KameHouseUserAuthenticationService kameHouseUserAuthenticationService;

  public void setKameHouseUserAuthenticationService(
      KameHouseUserAuthenticationService kameHouseUserAuthenticationService) {
    this.kameHouseUserAuthenticationService = kameHouseUserAuthenticationService;
  }

  public KameHouseUserAuthenticationService getKameHouseUserAuthenticationService() {
    return kameHouseUserAuthenticationService;
  }

  /** Returns the current session's status. */
  public SessionStatus get(HttpSession session) {
    logger.trace("getting the user's session status");
    Authentication authentication = getAuthentication();
    String username = authentication.getName();
    SessionStatus sessionStatus = new SessionStatus();
    sessionStatus.setUsername(StringEscapeUtils.escapeHtml4(username));
    sessionStatus.setServer(PropertiesUtils.getHostname());
    List<String> roles = new ArrayList<>();
    for (GrantedAuthority authority : authentication.getAuthorities()) {
      roles.add(authority.getAuthority());
    }
    sessionStatus.setRoles(roles);
    try {
      KameHouseUser kameHouseUser = kameHouseUserAuthenticationService.loadUserByUsername(username);
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

  /** Gets the Authentication object from the spring security context. */
  protected Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
