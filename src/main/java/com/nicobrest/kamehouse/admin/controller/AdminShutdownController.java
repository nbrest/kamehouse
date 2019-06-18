package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.AdminShutdownCommand;
import com.nicobrest.kamehouse.admin.service.AdminShutdownService;
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

/**
 * Controller class for the shutdown commands.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin")
public class AdminShutdownController {

  private static final Logger logger = LoggerFactory.getLogger(AdminShutdownController.class);

  @Autowired
  private AdminShutdownService adminShutdownService;

  /**
   * Shutdown the local server with the specified delay in seconds.
   */
  @RequestMapping(value = "/shutdown", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> setShutdown(
      @RequestBody AdminShutdownCommand adminShutdownCommand) {

    logger.trace("In controller /api/v1/admin/shutdown (POST)");
    List<SystemCommandOutput> commandOutputs = adminShutdownService.setShutdown(
        adminShutdownCommand);
    HttpStatus httpStatus = setHttpStatus(commandOutputs);
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }

  /**
   * Cancel a shutdown command.
   */
  @RequestMapping(value = "/shutdown", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> cancelShutdown() {

    logger.trace("In controller /api/v1/admin/shutdown (DELETE)");
    List<SystemCommandOutput> commandOutputs = adminShutdownService.cancelShutdown();
    HttpStatus httpStatus = setHttpStatus(commandOutputs);
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }

  /**
   * Get the status of a shutdown command.
   */
  @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusShutdown() {

    logger.trace("In controller /api/v1/admin/shutdown (GET)");
    List<SystemCommandOutput> commandOutputs = adminShutdownService.statusShutdown();
    HttpStatus httpStatus = setHttpStatus(commandOutputs);
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }
  
  /**
   * Set the HttpStatus based on the output of the system commands.
   */
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
