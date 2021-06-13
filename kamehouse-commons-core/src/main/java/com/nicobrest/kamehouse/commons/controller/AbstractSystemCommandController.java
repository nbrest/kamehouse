package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.AdminCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand.Output;
import com.nicobrest.kamehouse.commons.service.SystemCommandService;
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
  public ResponseEntity<List<Output>> execAdminCommand(AdminCommand adminCommand) {
    logger.trace("Executing admin command {}", adminCommand);
    List<Output> commandOutputs = systemCommandService.execute(adminCommand);
    return generateSystemCommandOutputsResponseEntity(commandOutputs);
  }

  /**
   * Generates a response entity for a list of SystemCommandOutputs.
   */
  public static ResponseEntity<List<Output>>
      generateSystemCommandOutputsResponseEntity(List<Output> commandOutputs) {
    HttpStatus httpStatus = HttpStatus.OK;
    for (Output commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    if (httpStatus.equals(HttpStatus.OK)) {
      STATIC_LOGGER.trace("Response {}", commandOutputs);
    } else {
      STATIC_LOGGER.error("Response {}", commandOutputs);
    }
    return new ResponseEntity<>(commandOutputs, httpStatus);
  }
}
