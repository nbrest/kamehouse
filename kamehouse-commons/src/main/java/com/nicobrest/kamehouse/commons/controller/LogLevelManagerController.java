package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.model.KameHouseGenericResponse;
import com.nicobrest.kamehouse.commons.service.LogLevelManagerService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to manage the log level of kamehouse.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/commons")
public class LogLevelManagerController extends AbstractController {

  private static final String DEFAULT_PACKAGE = "com.nicobrest.kamehouse";

  private LogLevelManagerService logLevelManagerService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public LogLevelManagerController(LogLevelManagerService logLevelManagerService) {
    this.logLevelManagerService = logLevelManagerService;
  }

  /**
   * Sets whether the request logger should include the payload or not.
   */
  @PutMapping(path = "/log-level/request-logger/payload")
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
   * Returns the current log level for the specified package or all packages with levels set, if no
   * package is specified.
   */
  @GetMapping(path = "/log-level")
  public ResponseEntity<List<String>> getLogLevel(
      @RequestParam(value = "package", required = false) String packageName) {
    String packageNameSanitized = StringUtils.sanitize(packageName);
    List<String> logLevelList = logLevelManagerService.getLogLevel(packageNameSanitized);
    return generateGetResponseEntity(logLevelList);
  }

  /**
   * Set the log level for the specified package.
   */
  @PutMapping(path = "/log-level")
  public ResponseEntity<List<String>> setLogLevel(
      @RequestParam(value = "level", required = true) String level,
      @RequestParam(value = "package", required = false) String packageName) {
    String levelSanitized = StringUtils.sanitize(level);
    String packageNameSanitized = StringUtils.sanitize(packageName);
    if (packageNameSanitized == null) {
      logger.info("Using default package {}", DEFAULT_PACKAGE);
      packageNameSanitized = DEFAULT_PACKAGE;
    }
    logLevelManagerService.validateLogLevel(levelSanitized);
    logLevelManagerService.setLogLevel(levelSanitized, packageNameSanitized);
    List<String> logLevelList = logLevelManagerService.getLogLevel(packageNameSanitized);
    return generatePutResponseEntity(logLevelList);
  }

  /**
   * Set kamehouse log levels to INFO.
   */
  @PutMapping(path = "/log-level/info")
  public ResponseEntity<List<String>> setKamehouseLogLevelsToInfo() {
    logLevelManagerService.setKamehouseLogLevelsToInfo();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generatePutResponseEntity(logLevelList);
  }

  /**
   * Set kamehouse log levels to DEBUG.
   */
  @PutMapping(path = "/log-level/debug")
  public ResponseEntity<List<String>> setKamehouseLogLevelsToDebug() {
    logLevelManagerService.setKamehouseLogLevelsToDebug();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generatePutResponseEntity(logLevelList);
  }

  /**
   * Set kamehouse log levels to TRACE.
   */
  @PutMapping(path = "/log-level/trace")
  public ResponseEntity<List<String>> setKamehouseLogLevelsToTrace() {
    logLevelManagerService.setKamehouseLogLevelsToTrace();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generatePutResponseEntity(logLevelList);
  }

  /**
   * Reset all log levels.
   */
  @DeleteMapping(path = "/log-level")
  public ResponseEntity<List<String>> resetLogLevels() {
    logLevelManagerService.resetLogLevels();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generateDeleteResponseEntity(logLevelList);
  }
}
