package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.TestDaemonCommand;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller to test AbstractKameHouseCommandController.
 */
@RestController
@RequestMapping(value = "/api/v1/unit-tests")
public class TestKameHouseCommandController extends AbstractKameHouseCommandController {

  public TestKameHouseCommandController(
      KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
  }

  /**
   * Executes a test kamehouse command.
   */
  @PostMapping(path = "/kamehouse-command")
  public ResponseEntity<List<KameHouseCommandResult>> execute() {
    return execKameHouseCommands(List.of(new TestDaemonCommand()));
  }
}
