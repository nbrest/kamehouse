package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;
import com.nicobrest.kamehouse.systemcommand.controller.AbstractSystemCommandController;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;

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
public class PowerManagementController extends AbstractSystemCommandController {

  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Shutdown the local server with the specified delay in seconds.
   */
  @PostMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> setShutdown(
      @RequestBody AdminCommand shutdownSetAdminCommand) {
    logger.trace("/api/v1/admin/power-management/shutdown (POST)");
    return executeAdminCommand(adminCommandService, shutdownSetAdminCommand,
        AdminCommand.SHUTDOWN_SET);
  }

  /**
   * Cancel a shutdown command.
   */
  @DeleteMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> cancelShutdown() {
    logger.trace("/api/v1/admin/power-management/shutdown (DELETE)");
    return executeAdminCommand(adminCommandService, AdminCommand.SHUTDOWN_CANCEL);
  }

  /**
   * Get the status of a shutdown command.
   */
  @GetMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusShutdown() {
    logger.trace("/api/v1/admin/power-management/shutdown (GET)");
    return executeAdminCommand(adminCommandService, AdminCommand.SHUTDOWN_STATUS);
  }

  /**
   * Suspend the server.
   */
  @PostMapping(path = "/suspend")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> suspend() {
    logger.trace("/api/v1/admin/power-management/suspend (POST)");
    return executeAdminCommand(adminCommandService, AdminCommand.SUSPEND);
  }
}
