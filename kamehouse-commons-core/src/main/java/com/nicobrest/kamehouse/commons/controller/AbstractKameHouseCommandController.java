package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Abstract controller superclass for controllers that execute and return the status of kamehouse
 * commands.
 *
 * @author nbrest
 */
public abstract class AbstractKameHouseCommandController extends AbstractController {

  private KameHouseCommandService kameHouseCommandService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  AbstractKameHouseCommandController(KameHouseCommandService kameHouseCommandService) {
    this.kameHouseCommandService = kameHouseCommandService;
  }

  /**
   * Executes the specified admin command and returns the kamehouse command result list.
   */
  public ResponseEntity<List<KameHouseCommandResult>> execKameHouseCommands(
      List<KameHouseCommand> kameHouseCommands) {
    logger.trace("Executing command {}", kameHouseCommands);
    List<KameHouseCommandResult> kameHouseCommandResults = kameHouseCommandService.execute(
        kameHouseCommands);
    return generateResponseEntity(kameHouseCommandResults);
  }

  /**
   * Generates a response entity for a list of KameHouseCommandResults.
   */
  public static ResponseEntity<List<KameHouseCommandResult>> generateResponseEntity(
      List<KameHouseCommandResult> kameHouseCommandResults) {
    HttpStatus httpStatus = HttpStatus.OK;
    for (KameHouseCommandResult kameHouseCommandResult : kameHouseCommandResults) {
      if (kameHouseCommandResult.getExitCode() > 0) {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    if (httpStatus.equals(HttpStatus.OK)) {
      STATIC_LOGGER.trace("Response {}", kameHouseCommandResults);
    } else {
      STATIC_LOGGER.error("Response {}", kameHouseCommandResults);
    }
    return new ResponseEntity<>(kameHouseCommandResults, httpStatus);
  }
}
