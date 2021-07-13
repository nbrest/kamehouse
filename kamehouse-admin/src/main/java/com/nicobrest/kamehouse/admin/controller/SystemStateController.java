package com.nicobrest.kamehouse.admin.controller;

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
   * Locks screen in the server running the application.
   */
  @GetMapping(path = "/uptime")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> uptime(HttpServletRequest request) {
    logTraceRequest(request);
    return execKameHouseSystemCommand(new UptimeKameHouseSystemCommand());
  }
}