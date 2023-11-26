package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.TestKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller to test AbstractSystemCommandController.
 */
@RestController
@RequestMapping(value = "/api/v1/unit-tests")
public class TestSystemCommandController extends AbstractSystemCommandController {

  public TestSystemCommandController(
      SystemCommandService systemCommandService) {
    super(systemCommandService);
  }

  /**
   * Executes a test system command.
   */
  @PostMapping(path = "/system-command")
  public ResponseEntity<List<SystemCommand.Output>> execute() {
    return execKameHouseSystemCommand(new TestKameHouseSystemCommand());
  }
}
