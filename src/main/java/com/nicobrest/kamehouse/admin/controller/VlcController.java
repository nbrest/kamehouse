package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;
import com.nicobrest.kamehouse.main.controller.AbstractController;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
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
public class VlcController extends AbstractController {
 
  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Start a vlc player in the local server.
   */
  @PostMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> startVlcPlayer(
      @RequestBody AdminCommand vlcStartAdminCommand) {
    logger.trace("In controller /api/v1/admin/vlc (POST)");
    if (!AdminCommand.VLC_START.equals(vlcStartAdminCommand.getCommand())) {
      throw new KameHouseInvalidCommandException("Invalid AdminCommand " + vlcStartAdminCommand
          .getCommand());
    }
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(vlcStartAdminCommand);
    return generateResponseEntity(commandOutputs);
  }

  /**
   * Stop vlc player in the local server.
   */
  @DeleteMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> stopVlcPlayer() {
    logger.trace("In controller /api/v1/admin/vlc (DELETE)");
    AdminCommand vlcStopAdminCommand = new AdminCommand(AdminCommand.VLC_STOP);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(vlcStopAdminCommand);
    return generateResponseEntity(commandOutputs);
  }

  /**
   * Get the status of vlc player in the local server.
   */
  @GetMapping(path = "/vlc")
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusVlcPlayer() {
    logger.trace("In controller /api/v1/admin/vlc (GET)");
    AdminCommand vlcStatusAdminCommand = new AdminCommand(AdminCommand.VLC_STATUS);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(vlcStatusAdminCommand);
    return generateResponseEntity(commandOutputs);
  }
}
