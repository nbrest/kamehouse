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
 * Controller class to start, stop and get the status of a local VLC player.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/admin")
public class VlcController extends AbstractSystemCommandController {
 
  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Start a vlc player in the local server.
   */
  @PostMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> startVlcPlayer(
      @RequestBody AdminCommand vlcStartAdminCommand) {
    logger.trace("/api/v1/admin/vlc (POST)");
    return executeAdminCommand(adminCommandService, vlcStartAdminCommand, AdminCommand.VLC_START);
  }

  /**
   * Stop vlc player in the local server.
   */
  @DeleteMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> stopVlcPlayer() {
    logger.trace("/api/v1/admin/vlc (DELETE)");
    return executeAdminCommand(adminCommandService, AdminCommand.VLC_STOP);
  }

  /**
   * Get the status of vlc player in the local server.
   */
  @GetMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusVlcPlayer() {
    logger.trace("/api/v1/admin/vlc (GET)");
    return executeAdminCommand(adminCommandService, AdminCommand.VLC_STATUS);
  }
}
