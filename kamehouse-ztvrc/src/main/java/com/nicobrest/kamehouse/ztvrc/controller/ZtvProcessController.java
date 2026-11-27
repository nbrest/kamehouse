package com.nicobrest.kamehouse.ztvrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandController;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.ztvrc.model.kamehousecommand.ZtvStartKameHouseCommand;
import com.nicobrest.kamehouse.ztvrc.model.kamehousecommand.ZtvStatusKameHouseCommand;
import com.nicobrest.kamehouse.ztvrc.model.kamehousecommand.ZtvStopKameHouseCommand;
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
 * Controller class to start, stop and get the status of a local ztv player.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/ztv-rc")
public class ZtvProcessController extends AbstractKameHouseCommandController {

  private static final String INPUT_FILE_SANITIZER_REGEX = "[\n\r\t\"<>?|]";

  public ZtvProcessController(
      KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
  }

  /**
   * Starts a ztv player in the local server.
   */
  @PostMapping(path = "/ztv-process")
  public ResponseEntity<List<KameHouseCommandResult>> startZtvPlayer(
      @RequestParam(value = "file", required = true) String file) {
    InputValidator.validateForbiddenCharsForShell(file);
    String fileSanitized = StringUtils.sanitize(file, INPUT_FILE_SANITIZER_REGEX);
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new ZtvStopKameHouseCommand(2));
    kameHouseCommands.add(new ZtvStartKameHouseCommand(fileSanitized));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Stops ztv player in the local server.
   */
  @DeleteMapping(path = "/ztv-process")
  public ResponseEntity<List<KameHouseCommandResult>> stopZtvPlayer() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new ZtvStopKameHouseCommand(2));
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Gets the status of ztv player in the local server.
   */
  @GetMapping(path = "/ztv-process")
  public ResponseEntity<List<KameHouseCommandResult>> statusZtvPlayer() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new ZtvStatusKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }
}
