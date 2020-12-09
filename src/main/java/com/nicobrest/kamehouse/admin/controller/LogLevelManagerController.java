package com.nicobrest.kamehouse.admin.controller;

import com.nicobrest.kamehouse.admin.service.LogLevelManagerService;
import com.nicobrest.kamehouse.main.controller.AbstractController;
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
@RequestMapping(value = "/api/v1/admin")
public class LogLevelManagerController extends AbstractController {

  private static final String DEFAULT_PACKAGE = "com.nicobrest.kamehouse";

  @Autowired
  private LogLevelManagerService logLevelManagerService;

  /**
   * Returns the current log level for the specified package or all packages with levels set, if
   * no package is specified.
   */
  @GetMapping(path = "/log-level")
  @ResponseBody
  public ResponseEntity<List<String>> getLogLevel(
      @RequestParam(value = "package", required = false) String packageName) {
    logger.info("/api/v1/admin/log-level (GET)");
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
    logger.info("/api/v1/admin/log-level (PUT)");
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
    logger.info("/api/v1/admin/log-level/debug (PUT)");

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
    logger.info("/api/v1/admin/log-level/trace (PUT)");

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
    logger.info("/api/v1/admin/log-level (DELETE)");

    logLevelManagerService.resetLogLevels();
    List<String> logLevelList = logLevelManagerService.getLogLevel(null);
    return generateDeleteResponseEntity(logLevelList);
  }
}
