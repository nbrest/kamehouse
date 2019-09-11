package com.nicobrest.kamehouse.admin.service;
 
import com.nicobrest.kamehouse.admin.model.ApplicationUser;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer to get the session status.
 * 
 * @author nbrest
 *
 */
@Service
public class SessionStatusService {

  private static final Logger logger = LoggerFactory.getLogger(SessionStatusService.class);
  
  @Autowired
  private ApplicationUserService applicationUserService;
  
  public void setApplicationUserService(ApplicationUserService applicationUserService) {
    this.applicationUserService = applicationUserService;
  }
  
  public ApplicationUserService getApplicationUserService() {
    return applicationUserService;
  }
  
  /**
   * Returns the current session's status.
   */
  public Map<String, Object> getSessionStatus() {
    
    logger.trace("Getting current session status");
    Authentication authentication = getAuthentication();
    String username = authentication.getName();
    WebAuthenticationDetails sessionDetails = (WebAuthenticationDetails) authentication
        .getDetails();
    Map<String, Object> sessionStatus = new HashMap<>();
    sessionStatus.put("username", StringEscapeUtils.escapeHtml(username));
    sessionStatus.put("session-id", sessionDetails.getSessionId());
    List<String> roles = new ArrayList<>();
    for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
      roles.add(grantedAuthority.getAuthority());
    }
    sessionStatus.put("roles", roles);
    try {
      ApplicationUser applicationUser = applicationUserService.loadUserByUsername(username);
      sessionStatus.put("firstName", applicationUser.getFirstName());
      sessionStatus.put("lastName", applicationUser.getLastName());
      sessionStatus.put("email", applicationUser.getEmail());
    } catch (UsernameNotFoundException e) {
      logger.trace(e.getMessage());
      sessionStatus.put("firstName", null);
      sessionStatus.put("lastName", null);
      sessionStatus.put("email", null);
    }
    return sessionStatus;
  }
  
  /**
   * Get the Authentication object from the spring security context.
   */
  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
