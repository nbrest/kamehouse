package com.nicobrest.kamehouse.utils;

import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ControllerUtils {

  public ResponseEntity<List<SystemCommandOutput>> generateResponseEntity(
      List<SystemCommandOutput> commandOutputs) {
    HttpStatus httpStatus = HttpStatus.OK;
    for (SystemCommandOutput commandOutput : commandOutputs) {
      if (commandOutput.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    ResponseEntity<List<SystemCommandOutput>> responseEntity =
        new ResponseEntity<List<SystemCommandOutput>>(commandOutputs, httpStatus);
    return responseEntity;
  }
}
