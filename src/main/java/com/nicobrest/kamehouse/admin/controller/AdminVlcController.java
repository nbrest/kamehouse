package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.CommandOutput;
import com.nicobrest.kamehouse.admin.service.AdminVlcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/v1/admin")
public class AdminVlcController {

  private static final Logger logger = LoggerFactory.getLogger(AdminVlcController.class);

  @Autowired
  private AdminVlcService adminVlcService;

  /**
   * Start a vlc player.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<CommandOutput> startVlcPlayer() {

    logger.trace("Began start vlc player");
    ResponseEntity<CommandOutput> responseEntity;
    CommandOutput commandOutput = adminVlcService.startVlcPlayer();
    if (commandOutput.getExitCode() <= 0) {
      responseEntity = new ResponseEntity<CommandOutput>(commandOutput, HttpStatus.OK);
    } else {
      responseEntity = new ResponseEntity<CommandOutput>(commandOutput,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.trace("Finished start vlc player");
    return responseEntity;
  }

  /**
   * Stop vlc player.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<CommandOutput> stopVlcPlayer() {

    logger.trace("Began stop vlc player");
    ResponseEntity<CommandOutput> responseEntity;
    CommandOutput commandOutput = adminVlcService.stopVlcPlayer();
    if (commandOutput.getExitCode() <= 0) {
      responseEntity = new ResponseEntity<CommandOutput>(commandOutput, HttpStatus.OK);
    } else {
      responseEntity = new ResponseEntity<CommandOutput>(commandOutput,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.trace("Finished stop vlc player");
    return responseEntity;
  }

  /**
   * Get the status of vlc player.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<CommandOutput> statusVlcPlayer() {

    logger.trace("Began status vlc player");
    ResponseEntity<CommandOutput> responseEntity;
    CommandOutput commandOutput = adminVlcService.statusVlcPlayer();
    if (commandOutput.getExitCode() <= 0) {
      responseEntity = new ResponseEntity<CommandOutput>(commandOutput, HttpStatus.OK);
    } else {
      responseEntity = new ResponseEntity<CommandOutput>(commandOutput,
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.trace("Finished status vlc player");
    return responseEntity;
  }

}
