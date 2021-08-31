package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.DfKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.FreeKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdRestartKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.HttpdStatusKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.UptimeKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractSystemCommandController;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to execute commands to check the system's state.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/admin/system-state")
public class SystemStateController extends AbstractSystemCommandController {

  /** Gets the uptime of the server running kamehouse. */
  @GetMapping(path = "/uptime")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> uptime() {
    return execKameHouseSystemCommand(new UptimeKameHouseSystemCommand());
  }

  /** Gets the available memory of the server running kamehouse. */
  @GetMapping(path = "/free")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> free() {
    return execKameHouseSystemCommand(new FreeKameHouseSystemCommand());
  }

  /** Gets the available disk space of the server running kamehouse. */
  @GetMapping(path = "/df")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> df() {
    return execKameHouseSystemCommand(new DfKameHouseSystemCommand());
  }

  /** Gets the status of the httpd running in the same server as the current tomcat. */
  @GetMapping(path = "/httpd")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> httpdGetStatus() {
    return execKameHouseSystemCommand(new HttpdStatusKameHouseSystemCommand());
  }

  /** Restart apache httpd server. */
  @PostMapping(path = "/httpd")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> restartHttpd() {
    return execKameHouseSystemCommand(new HttpdRestartKameHouseSystemCommand());
  }
}
