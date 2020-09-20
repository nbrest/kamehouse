package com.nicobrest.kamehouse.admin.controller;
 
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownCancelAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ShutdownStatusAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.SuspendAdminCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand;

import com.nicobrest.kamehouse.admin.service.PowerManagementService;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

  private static final String BASE_URL = "/api/v1/admin/power-management";

  @Autowired
  PowerManagementService powerManagementService;

  /**
   * Shutdowns the local server with the specified delay in seconds.
   */
  @PostMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>>
      setShutdown(@RequestParam(value = "delay", required = true) Integer delay) {
    logger.trace("{}/shutdown?delay={} (POST)", BASE_URL, delay);
    return execAdminCommand(new ShutdownAdminCommand(delay));
  }

  /**
   * Cancels a shutdown command.
   */
  @DeleteMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> cancelShutdown() {
    logger.trace("{}/shutdown (DELETE)", BASE_URL);
    return execAdminCommand(new ShutdownCancelAdminCommand());
  }

  /**
   * Gets the status of a shutdown command.
   */
  @GetMapping(path = "/shutdown")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> statusShutdown() {
    logger.trace("{}/shutdown (GET)", BASE_URL);
    return execAdminCommand(new ShutdownStatusAdminCommand());
  }

  /**
   * Suspends the server.
   */
  @PostMapping(path = "/suspend")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> suspend() {
    logger.trace("{}/suspend (POST)", BASE_URL);
    return execAdminCommand(new SuspendAdminCommand());
  }

  /**
   * Wake on lan the specified server or mac address.
   */
  @PostMapping(path = "/wol")
  @ResponseBody
  public ResponseEntity<Void> wakeOnLan(
      @RequestParam(value = "server", required = false) String server,
      @RequestParam(value = "mac", required = false) String mac,
      @RequestParam(value = "broadcast", required = false) String broadcast) {
    if (server != null) {
      logger.trace("{}/wol?server={} (POST)", BASE_URL, server);
      powerManagementService.wakeOnLan(server);
    } else if (mac != null && broadcast != null) {
      logger.trace("{}/wol?mac={}&broadcast={} (POST)", BASE_URL, mac, broadcast);
      powerManagementService.wakeOnLan(mac, broadcast);
    } else {
      throw new KameHouseBadRequestException("server OR mac and broadcast parameters are required");
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
