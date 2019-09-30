package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.SystemCommandOutput;
import com.nicobrest.kamehouse.admin.model.admincommand.VlcStartAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.VlcStatusAdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.VlcStopAdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;

import org.springframework.beans.factory.annotation.Autowired;
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
  public ResponseEntity<List<SystemCommandOutput>> startVlcPlayer(@RequestParam(value = "file",
      required = false) String file) {
    logger.trace("/api/v1/admin/vlc?file=value (POST)");
    return executeAdminCommand(adminCommandService, new VlcStartAdminCommand(file));
  }

  /**
   * Stop vlc player in the local server.
   */
  @DeleteMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> stopVlcPlayer() {
    logger.trace("/api/v1/admin/vlc (DELETE)");
    return executeAdminCommand(adminCommandService, new VlcStopAdminCommand());
  }

  /**
   * Get the status of vlc player in the local server.
   */
  @GetMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusVlcPlayer() {
    logger.trace("/api/v1/admin/vlc (GET)");
    return executeAdminCommand(adminCommandService, new VlcStatusAdminCommand());
  }
}
