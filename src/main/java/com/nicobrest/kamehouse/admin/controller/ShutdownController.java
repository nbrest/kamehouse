package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.utils.ControllerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ShutdownController {

  private static final Logger logger = LoggerFactory.getLogger(ShutdownController.class);

  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Shutdown the local server with the specified delay in seconds.
   */
  @RequestMapping(value = "/shutdown", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> setShutdown(
      @RequestBody AdminCommand shutdownSetAdminCommand) {

    logger.trace("In controller /api/v1/admin/shutdown (POST)");
    if (!AdminCommand.SHUTDOWN_SET.equals(shutdownSetAdminCommand.getCommand())) {
      throw new KameHouseInvalidCommandException("Invalid AdminCommand " + shutdownSetAdminCommand
          .getCommand());
    }
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        shutdownSetAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }

  /**
   * Cancel a shutdown command.
   */
  @RequestMapping(value = "/shutdown", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> cancelShutdown() {

    logger.trace("In controller /api/v1/admin/shutdown (DELETE)");
    AdminCommand shutdownCancelAdminCommand = new AdminCommand(AdminCommand.SHUTDOWN_CANCEL);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        shutdownCancelAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }

  /**
   * Get the status of a shutdown command.
   */
  @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusShutdown() {

    logger.trace("In controller /api/v1/admin/shutdown (GET)");
    AdminCommand shutdownStatusAdminCommand = new AdminCommand(AdminCommand.SHUTDOWN_STATUS);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        shutdownStatusAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }
}
