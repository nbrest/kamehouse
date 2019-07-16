package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.utils.ControllerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class VlcController {

  private static final Logger logger = LoggerFactory.getLogger(VlcController.class);

  @Autowired
  private AdminCommandService adminCommandService;

  /**
   * Start a vlc player in the local server.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> startVlcPlayer(
      @RequestBody AdminCommand vlcStartAdminCommand) {

    logger.trace("In controller /api/v1/admin/vlc (POST)");
    if (!AdminCommand.VLC_START.equals(vlcStartAdminCommand.getCommand())) {
      throw new KameHouseInvalidCommandException("Invalid AdminCommand " + vlcStartAdminCommand
          .getCommand());
    }
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(vlcStartAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }

  /**
   * Stop vlc player in the local server.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> stopVlcPlayer() {

    logger.trace("In controller /api/v1/admin/vlc (DELETE)");
    AdminCommand vlcStopAdminCommand = new AdminCommand(AdminCommand.VLC_STOP);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(vlcStopAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }

  /**
   * Get the status of vlc player in the local server.
   */
  @RequestMapping(value = "/vlc", method = RequestMethod.GET)
  @ResponseBody
  public ResponseEntity<List<SystemCommandOutput>> statusVlcPlayer() {

    logger.trace("In controller /api/v1/admin/vlc (GET)");
    AdminCommand vlcStatusAdminCommand = new AdminCommand(AdminCommand.VLC_STATUS);
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(vlcStatusAdminCommand);
    ResponseEntity<List<SystemCommandOutput>> responseEntity = ControllerUtils
        .generateResponseEntity(commandOutputs);
    return responseEntity;
  }
}
