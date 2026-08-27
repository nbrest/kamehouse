package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.kamehousecommand.KameHouseDesktopStartupKameHouseCommand;
import com.nicobrest.kamehouse.admin.model.kamehousecommand.KameHouseDesktopStopKameHouseCommand;
import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandController;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller class to start and stop kamehouse-desktop.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/admin/kamehouse-desktop")
public class KameHouseDesktopController extends AbstractKameHouseCommandController {

  /**
   * KameHouse destkop controller.
   */
  public KameHouseDesktopController(
      KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
  }

  /**
   * Starts kamehouse-desktop.
   */
  @PostMapping()
  public ResponseEntity<List<KameHouseCommandResult>> startKameHouseDesktop() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new KameHouseDesktopStartupKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }

  /**
   * Stops kamehouse-desktop.
   */
  @DeleteMapping()
  public ResponseEntity<List<KameHouseCommandResult>> stopKameHouseDesktop() {
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    kameHouseCommands.add(new KameHouseDesktopStopKameHouseCommand());
    return execKameHouseCommands(kameHouseCommands);
  }
}
