package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.service.SystemCommandServiceTest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Test controller to test AbstractSystemCommandController.
 */
@Controller
@RequestMapping(value = "/api/v1/unit-tests")
public class TestSystemCommandController extends AbstractSystemCommandController {

  /**
   * Executes a test system command.
   */
  @PostMapping(path = "/system-command")
  @ResponseBody
  public ResponseEntity<List<SystemCommand.Output>> execute() {
    return execKameHouseSystemCommand(new SystemCommandServiceTest.TestKameHouseSystemCommand());
  }
}
