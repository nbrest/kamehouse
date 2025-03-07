package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandController;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStartKameHouseCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStatusKameHouseCommand;
import com.nicobrest.kamehouse.vlcrc.model.kamehousecommand.VlcStopKameHouseCommand;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to start, stop and get the status of a local VLC player.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/vlc-rc")
public class VlcProcessController extends AbstractKameHouseCommandController {

  private static final String INPUT_FILE_SANITIZER_REGEX = "[\n\r\t\"<>?|]";

  public VlcProcessController(
      KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
  }

  /**
   * Starts a vlc player in the local server.
   */
  @PostMapping(path = "/vlc-process")
  public ResponseEntity<List<KameHouseCommandResult>> startVlcPlayer(
      @RequestParam(value = "file", required = true) String file) {
    String fileSanitized = StringUtils.sanitize(file, INPUT_FILE_SANITIZER_REGEX);
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new VlcStopKameHouseCommand(2));
    kameHouseCommands.add(new VlcStartKameHouseCommand(fileSanitized));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Stops vlc player in the local server.
   */
  @DeleteMapping(path = "/vlc-process")
  public ResponseEntity<List<KameHouseCommandResult>> stopVlcPlayer() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new VlcStopKameHouseCommand(2));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Gets the status of vlc player in the local server.
   */
  @GetMapping(path = "/vlc-process")
  public ResponseEntity<List<KameHouseCommandResult>> statusVlcPlayer() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new VlcStatusKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }
}
