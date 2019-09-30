package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.model.SystemCommandOutput;
import com.nicobrest.kamehouse.admin.model.admincommand.AdminCommand;
import com.nicobrest.kamehouse.admin.service.AdminCommandService;
import com.nicobrest.kamehouse.main.controller.AbstractController;

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

  /**
   * Executes the specified admin command and returns the sytem command ouputs
   * list.
   */
  public static ResponseEntity<List<SystemCommandOutput>> executeAdminCommand(
      AdminCommandService adminCommandService, AdminCommand adminCommand) {
    List<SystemCommandOutput> commandOutputs = adminCommandService.execute(adminCommand);
    return generateSystemCommandOutputsResponseEntity(commandOutputs);
  }

  /**
   * Generates a response entity for a list of SystemCommandOutputs.
   */
  public static ResponseEntity<List<SystemCommandOutput>>
      generateSystemCommandOutputsResponseEntity(List<SystemCommandOutput> commandOutputs) {
    HttpStatus httpStatus = HttpStatus.OK;
    for (SystemCommandOutput commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    return new ResponseEntity<>(commandOutputs, httpStatus);
  }
}
