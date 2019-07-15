package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.service.AdminLockScreenService;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/admin")
public class AdminLockScreenController {
  
  private static final Logger logger = LoggerFactory.getLogger(AdminLockScreenController.class);
  
  @Autowired
  private AdminLockScreenService adminLockScreenService;

  /**
   * Lock screen in the server running the application.
   */
  @RequestMapping(value = "/lock-screen", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> lockScreen() {

    logger.trace("In controller /api/v1/admin/lock-screen (POST)");
    List<SystemCommandOutput> commandOutputs = adminLockScreenService.lockScreen();
    HttpStatus httpStatus = setHttpStatus(commandOutputs);
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }
  
  /**
   * Unlock screen in the server running the application.
   */
  @RequestMapping(value = "/unlock-screen", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> unlockScreen() {

    logger.trace("In controller /api/v1/admin/unlock-screen (POST)");
    List<SystemCommandOutput> commandOutputs = adminLockScreenService.unlockScreen();
    HttpStatus httpStatus = setHttpStatus(commandOutputs);
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }
  
  /**
   * Set the HttpStatus based on the output of the system commands.
   */
  //TODO: This method is in multiple controllers. Move it to a common place.
  private HttpStatus setHttpStatus(List<SystemCommandOutput> commandOutputs) {
    HttpStatus httpStatus = HttpStatus.OK;
    for (SystemCommandOutput commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    return httpStatus;
  }
}