package com.nicobrest.kamehouse.controller;

import com.nicobrest.kamehouse.service.SessionStatusService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Controller to obtain current session information.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/session")
public class SessionStatusController {

  private static final Logger logger = LoggerFactory.getLogger(SessionStatusController.class);

  @Autowired
  private SessionStatusService sessionStatusService;

  /**
   * Returns the current session's status.
   */
  @RequestMapping(value = "/status", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<Map<String, Object>> getSessionStatus() {

    logger.trace("In controller /api/v1/session/status (GET)");
    Map<String, Object> sessionStatus = sessionStatusService.getSessionStatus();
    return new ResponseEntity<Map<String, Object>>(sessionStatus, HttpStatus.OK);
  }
}
