package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.DfKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.FreeKameHouseSystemCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.UptimeKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractSystemCommandController;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Controller to execute commands to check the system's state.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin/system-state")
public class SystemStateController extends AbstractSystemCommandController {

  /**
   * Gets the uptime of the server running kamehouse.
   */
  @GetMapping(path = "/uptime")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> uptime(HttpServletRequest request) {
    logTraceRequest(request);
    return execKameHouseSystemCommand(new UptimeKameHouseSystemCommand());
  }

  /**
   * Gets the available memory of the server running kamehouse.
   */
  @GetMapping(path = "/free")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> free(HttpServletRequest request) {
    logTraceRequest(request);
    return execKameHouseSystemCommand(new FreeKameHouseSystemCommand());
  }

  /**
   * Gets the available disk space of the server running kamehouse.
   */
  @GetMapping(path = "/df")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> df(HttpServletRequest request) {
    logTraceRequest(request);
    return execKameHouseSystemCommand(new DfKameHouseSystemCommand());
  }
}