package com.nicobrest.kamehouse.systemcommand.controller;

import com.nicobrest.kamehouse.main.controller.AbstractController;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;

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
