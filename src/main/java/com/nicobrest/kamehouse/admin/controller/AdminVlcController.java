package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.AdminVlcCommand;
import com.nicobrest.kamehouse.admin.service.AdminVlcService;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/api/v1/admin")
public class AdminVlcController {

  private static final Logger logger = LoggerFactory.getLogger(AdminVlcController.class);

  @Autowired
  private AdminVlcService adminVlcService;

  /**
   * Start a vlc player in the local server.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> startVlcPlayer(
      @RequestBody AdminVlcCommand adminVlcCommand) {

    logger.trace("In controller /api/v1/admin/vlc (POST)");
    List<SystemCommandOutput> commandOutputs = adminVlcService.startVlcPlayer(adminVlcCommand);
    HttpStatus httpStatus = HttpStatus.OK;
    for (SystemCommandOutput commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }

  /**
   * Stop vlc player in the local server.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> stopVlcPlayer() {

    logger.trace("In controller /api/v1/admin/vlc (DELETE)"); 
    List<SystemCommandOutput> commandOutputs = adminVlcService.stopVlcPlayer();
    HttpStatus httpStatus = HttpStatus.OK;
    for (SystemCommandOutput commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }

  /**
   * Get the status of vlc player in the local server.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusVlcPlayer() {

    logger.trace("In controller /api/v1/admin/vlc (GET)"); 
    List<SystemCommandOutput> commandOutputs = adminVlcService.statusVlcPlayer();
    HttpStatus httpStatus = HttpStatus.OK;
    for (SystemCommandOutput commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus); 
    return responseEntity;
  }
}
