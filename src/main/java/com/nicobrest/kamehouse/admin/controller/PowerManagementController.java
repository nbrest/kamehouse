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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller class for the shutdown commands.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin/power-management")
public class PowerManagementController {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Shutdown the local server with the specified delay in seconds.
   */
  @PostMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> setShutdown(
      @RequestBody AdminCommand shutdownSetAdminCommand) {

    logger.trace("In controller /api/v1/admin/power-management/shutdown (POST)");
    if (!AdminCommand.SHUTDOWN_SET.equals(shutdownSetAdminCommand.getCommand())) {
      throw new KameHouseInvalidCommandException("Invalid AdminCommand " + shutdownSetAdminCommand
          .getCommand());
    }
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        shutdownSetAdminCommand);
    return ControllerUtils.generateResponseEntity(commandOutputs);
  }

  /**
   * Cancel a shutdown command.
   */
  @DeleteMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> cancelShutdown() {

    logger.trace("In controller /api/v1/admin/power-management/shutdown (DELETE)");
    AdminCommand shutdownCancelAdminCommand = new AdminCommand(AdminCommand.SHUTDOWN_CANCEL);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        shutdownCancelAdminCommand); 
    return ControllerUtils.generateResponseEntity(commandOutputs);
  }

  /**
   * Get the status of a shutdown command.
   */
  @GetMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusShutdown() {

    logger.trace("In controller /api/v1/admin/power-management/shutdown (GET)");
    AdminCommand shutdownStatusAdminCommand = new AdminCommand(AdminCommand.SHUTDOWN_STATUS);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        shutdownStatusAdminCommand);
    return ControllerUtils.generateResponseEntity(commandOutputs);
  }
  
  /**
   * Suspend the server.
   */
  @PostMapping(path = "/suspend")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> suspend() {

    logger.trace("In controller /api/v1/admin/power-management/suspend (POST)");
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SUSPEND);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(
        adminCommand);
    return ControllerUtils.generateResponseEntity(commandOutputs);
  }
}
