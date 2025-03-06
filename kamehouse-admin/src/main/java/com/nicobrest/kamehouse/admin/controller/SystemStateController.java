package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.DfKameHouseCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.FreeKameHouseCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdStartKameHouseCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdStatusKameHouseCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdStopKameHouseCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.TopKameHouseCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.UptimeKameHouseCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandController;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to execute commands to check the system's state.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/admin/system-state")
public class SystemStateController extends AbstractKameHouseCommandController {

  public SystemStateController(
      KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
  }

  /**
   * Gets the uptime of the server running kamehouse.
   */
  @GetMapping(path = "/uptime")
  public ResponseEntity<List<KameHouseCommandResult>> uptime() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new UptimeKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Gets the available memory of the server running kamehouse.
   */
  @GetMapping(path = "/free")
  public ResponseEntity<List<KameHouseCommandResult>> free() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new FreeKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Gets the available disk space of the server running kamehouse.
   */
  @GetMapping(path = "/df")
  public ResponseEntity<List<KameHouseCommandResult>> df() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new DfKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Gets the available memory of the server running kamehouse.
   */
  @GetMapping(path = "/top")
  public ResponseEntity<List<KameHouseCommandResult>> top() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new TopKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Gets the status of the httpd running in the same server as the current tomcat.
   */
  @GetMapping(path = "/httpd")
  public ResponseEntity<List<KameHouseCommandResult>> httpdGetStatus() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new HttpdStatusKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Restart apache httpd server.
   */
  @PostMapping(path = "/httpd")
  public ResponseEntity<List<KameHouseCommandResult>> restartHttpd() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new HttpdStopKameHouseCommand());
    kameHouseCommands.add(new HttpdStartKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }
}
