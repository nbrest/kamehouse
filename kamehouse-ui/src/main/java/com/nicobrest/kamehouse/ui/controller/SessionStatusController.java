package com.nicobrest.kamehouse.ui.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.ui.model.SessionStatus;
import com.nicobrest.kamehouse.ui.service.SessionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller to obtain current session information.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/ui/session")
public class SessionStatusController extends AbstractController {

  @Autowired
  private SessionStatusService sessionStatusService;

  /**
   * Returns the current session's status.
   */
  @GetMapping(path = "/status")
  @ResponseBody
  public ResponseEntity<SessionStatus> getSessionStatus(HttpServletRequest request) {
    SessionStatus sessionStatus = sessionStatusService.get(request.getSession());
    return generateGetResponseEntity(sessionStatus);
  }
}
