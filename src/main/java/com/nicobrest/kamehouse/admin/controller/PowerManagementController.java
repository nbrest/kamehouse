package com.nicobrest.kamehouse.admin.controller;
 
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownCancelAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownStatusAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.SuspendAdminCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  /**
   * Shutdowns the local server with the specified delay in seconds.
   */
  @PostMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>>
      setShutdown(@RequestParam(value = "delay", required = true) Integer delay) {
    logger.trace("/api/v1/admin/power-management/shutdown?delay=value (POST)");
    return execAdminCommand(new ShutdownAdminCommand(delay));
  }

  /**
   * Cancels a shutdown command.
   */
  @DeleteMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> cancelShutdown() {
    logger.trace("/api/v1/admin/power-management/shutdown (DELETE)");
    return execAdminCommand(new ShutdownCancelAdminCommand());
  }

  /**
   * Gets the status of a shutdown command.
   */
  @GetMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> statusShutdown() {
    logger.trace("/api/v1/admin/power-management/shutdown (GET)");
    return execAdminCommand(new ShutdownStatusAdminCommand());
  }

  /**
   * Suspends the server.
   */
  @PostMapping(path = "/suspend")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> suspend() {
    logger.trace("/api/v1/admin/power-management/suspend (POST)");
    return execAdminCommand(new SuspendAdminCommand());
  }
}
