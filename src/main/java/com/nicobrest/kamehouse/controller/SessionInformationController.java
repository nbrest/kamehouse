package com.nicobrest.kamehouse.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to obtain current session information.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/session")
public class SessionInformationController {

  private static final Logger logger = LoggerFactory.getLogger(SessionInformationController.class);
  
  /**
   * Returns the current session's status.
   */
  @RequestMapping(value = "/status", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getSessionStatus() {

    logger.trace("Getting session status");
    Authentication authentication = getAuthentication();
    String username = authentication.getName();
    WebAuthenticationDetails sessionDetails = (WebAuthenticationDetails) authentication
        .getDetails();
    Map<String, Object> sessionInformation = new HashMap<String, Object>();
    sessionInformation.put("username", username);
    //sessionInformation.put("remote-address", sessionDetails.getRemoteAddress());
    sessionInformation.put("session-id", sessionDetails.getSessionId());
    List<String> roles = new ArrayList<String>();
    for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
      roles.add(grantedAuthority.getAuthority());
    }
    sessionInformation.put("roles", roles);

    return new ResponseEntity<Map<String, Object>>(sessionInformation, HttpStatus.OK);
  }
  
  /**
   * Get the Authentication object from the spring security context.
   */
  private Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }
}
