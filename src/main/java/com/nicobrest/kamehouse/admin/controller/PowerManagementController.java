package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.SystemCommandOutput;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownCancelAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownStatusAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.SuspendAdminCommand;

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
   * Shutdown the local server with the specified delay in seconds.
   */
  @PostMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>>
      setShutdown(@RequestParam(value = "delay", required = true) Integer delay) {
    logger.trace("/api/v1/admin/power-management/shutdown?delay=value (POST)");
    return executeAdminCommand(new ShutdownAdminCommand(delay));
  }

  /**
   * Cancel a shutdown command.
   */
  @DeleteMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> cancelShutdown() {
    logger.trace("/api/v1/admin/power-management/shutdown (DELETE)");
    return executeAdminCommand(new ShutdownCancelAdminCommand());
  }

  /**
   * Get the status of a shutdown command.
   */
  @GetMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusShutdown() {
    logger.trace("/api/v1/admin/power-management/shutdown (GET)");
    return executeAdminCommand(new ShutdownStatusAdminCommand());
  }

  /**
   * Suspend the server.
   */
  @PostMapping(path = "/suspend")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> suspend() {
    logger.trace("/api/v1/admin/power-management/suspend (POST)");
    return executeAdminCommand(new SuspendAdminCommand());
  }
}
