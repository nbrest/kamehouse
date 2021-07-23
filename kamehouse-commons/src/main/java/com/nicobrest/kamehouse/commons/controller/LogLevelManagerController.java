package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.commons.service.LogLevelManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller to manage the log level of kamehouse.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/commons")
public class LogLevelManagerController extends AbstractController {

  private static final String DEFAULT_PACKAGE = "com.nicobrest.kamehouse";

  @Autowired
  private LogLevelManagerService logLevelManagerService;

  /**
   * Sets whether the request logger should include the payload or not.
   */
  @PutMapping(path = "/log-level/request-logger/payload")
  @ResponseBody
  public ResponseEntity<KameHouseGenericResponse> logPayload(
      @RequestParam(value = "logPayload", required = true) Boolean logPayload) {
    String message = "Setting request logger to log payload to: " + logPayload;
    logger.info(message);
    logLevelManagerService.setIncludePayload(logPayload);
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(message);
    return generatePutResponseEntity(response);
  }

  /**
   * Sets whether the request logger should include the headers or not.
   */
  @PutMapping(path = "/log-level/request-logger/headers")
  @ResponseBody
  public ResponseEntity<KameHouseGenericResponse> logHeaders(
      @RequestParam(value = "logHeaders", required = true) Boolean logHeaders) {
    String message = "Setting request logger to log headers to: " + logHeaders;
    logger.info(message);
    logLevelManagerService.setIncludeHeaders(logHeaders);
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(message);
    return generatePutResponseEntity(response);
  }

  /**
   * Sets whether the request logger should include the client info or not.
   */
  @PutMapping(path = "/log-level/request-logger/client-info")
  @ResponseBody
  public ResponseEntity<KameHouseGenericResponse> logClientInfo(
      @RequestParam(value = "logClientInfo", required = true) Boolean logClientInfo) {
    String message = "Setting request logger to log the client info to: " + logClientInfo;
    logger.info(message);
    logLevelManagerService.setIncludeClientInfo(logClientInfo);
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(message);
    return generatePutResponseEntity(response);
  }

  /**
   * Sets whether the request logger should include the query string or not.
   */
  @PutMapping(path = "/log-level/request-logger/query-string")
  @ResponseBody
  public ResponseEntity<KameHouseGenericResponse> logQueryString(
      @RequestParam(value = "logQueryString", required = true) Boolean logQueryString) {
    String message = "Setting request logger to log the query string to: " + logQueryString;
    logger.info(message);
    logLevelManagerService.setIncludeQueryString(logQueryString);
    KameHouseGenericResponse response = new KameHouseGenericResponse();
    response.setMessage(message);
    return generatePutResponseEntity(response);
  }

  /**
   * Returns the current log level for the specified package or all packages with levels set, if
   * no package is specified.
   */
  @GetMapping(path = "/log-level")
  @ResponseBody
  public ResponseEntity<List<String>> getLogLevel(
      @RequestParam(value = "package", required = false) String packageName) {
    logger.info("/api/v1/commons/log-level (GET)");
    List<String> logLevelList = logLevelManagerService.getLogLevel(packageName);
    return generateGetResponseEntity(logLevelList);
  }

  /**
   * Set the log level for the specified package.
   */
  @PutMapping(path = "/log-level")
  @ResponseBody
  public ResponseEntity<List<String>> setLogLevel(
      @RequestParam(value = "level", required = true) String level,
      @RequestParam(value = "package", required = false) String packageName) {
    logger.info("/api/v1/commons/log-level (PUT)");
    if (packageName == null) {
      logger.info("Using default package {}", DEFAULT_PACKAGE);
      packageName = DEFAULT_PACKAGE;
    }
    logLevelManagerService.validateLogLevel(level);
    logLevelManagerService.setLogLevel(level, packageName);
    List<String> logLevelList = logLevelManagerService.getLogLevel(packageName);
    return generatePutResponseEntity(logLevelList);
  }

  /**
   * Set kamehouse log levels to DEBUG.
   */
  @PutMapping(path = "/log-level/debug")
  @ResponseBody
  public ResponseEntity<List<String>> setKamehouseLogLevelsToDebug() {
    logger.info("/api/v1/commons/log-level/debug (PUT)");

    logLevelManagerService.setKamehouseLogLevelsToDebug();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generatePutResponseEntity(logLevelList);
  }

  /**
   * Set kamehouse log levels to TRACE.
   */
  @PutMapping(path = "/log-level/trace")
  @ResponseBody
  public ResponseEntity<List<String>> setKamehouseLogLevelsToTrace() {
    logger.info("/api/v1/commons/log-level/trace (PUT)");

    logLevelManagerService.setKamehouseLogLevelsToTrace();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generatePutResponseEntity(logLevelList);
  }

  /**
   * Reset all log levels.
   */
  @DeleteMapping(path = "/log-level")
  @ResponseBody
  public ResponseEntity<List<String>> resetLogLevels() {
    logger.info("/api/v1/commons/log-level (DELETE)");

    logLevelManagerService.resetLogLevels();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generateDeleteResponseEntity(logLevelList);
  }
}
