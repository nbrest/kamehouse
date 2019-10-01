package com.nicobrest.kamehouse.admin.controller;
 
import com.nicobrest.kamehouse.admin.model.admincommand.AdminCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand.Output;
import com.nicobrest.kamehouse.admin.service.SystemCommandService;
import com.nicobrest.kamehouse.main.controller.AbstractController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Abstract controller superclass for controllers that execute and return the
 * status of system commands.
 * 
 * @author nbrest
 *
 */
public class AbstractSystemCommandController extends AbstractController {

  @Autowired
  private SystemCommandService systemCommandService;

  /**
   * Executes the specified admin command and returns the sytem command ouputs
   * list.
   */
  public ResponseEntity<List<SystemCommand.Output>> executeAdminCommand(AdminCommand adminCommand) {
    List<SystemCommand.Output> commandOutputs = systemCommandService.execute(adminCommand);
    return generateSystemCommandOutputsResponseEntity(commandOutputs);
  }

  /**
   * Generates a response entity for a list of SystemCommandOutputs.
   */
  public static ResponseEntity<List<SystemCommand.Output>>
      generateSystemCommandOutputsResponseEntity(List<SystemCommand.Output> commandOutputs) {
    HttpStatus httpStatus = HttpStatus.OK;
    for (Output commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    return new ResponseEntity<>(commandOutputs, httpStatus);
  }
}
