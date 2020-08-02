package com.nicobrest.kamehouse.admin.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.nicobrest.kamehouse.main.controller.AbstractController;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller to manage the log level of kamehouse.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/")
public class LogLevelController extends AbstractController {

  private static final String DEFAULT_PACKAGE = "com.nicobrest";
  private static final List<String> LOG_LEVELS = Arrays.asList("ERROR", "WARN", "INFO", "DEBUG",
      "TRACE");

  /**
   * Returns the current log level for kamehouse.
   */
  @GetMapping(path = "/log-level")
  @ResponseBody
  public ResponseEntity<List<String>> getLogLevelGet(
      @RequestParam(value = "package", required = false) String packageName) {
    logger.info("/api/v1/log-level (GET)");
    List<String> logLevel = getLogLevel(packageName);
    return generateGetResponseEntity(logLevel);
  }

  /**
   * Set the log level for the specified package.
   */
  @PostMapping(path = "/log-level")
  @ResponseBody
  public ResponseEntity<List<String>> setLogLevelPost(
      @RequestParam(value = "level", required = true) String level,
      @RequestParam(value = "package", required = false) String packageName) {
    logger.info("/api/v1/log-level (POST)", level, packageName);
    if (packageName == null) {
      logger.info("Using default package {}", DEFAULT_PACKAGE);
      packageName = DEFAULT_PACKAGE;
    }
    validateLogLevel(level);
    setLogLevel(level, packageName);
    List<String> logLevel = getLogLevel(packageName);
    return generatePostResponseEntity(logLevel);
  }

  /**
   * Set the log level for the specified package.
   */
  private void setLogLevel(String level, String packageName) {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger logger = loggerContext.getLogger(packageName);
    logger.setLevel(Level.toLevel(level));
  }

  /**
   * Get the current log level for the specified package.
   */
  private List<String> getLogLevel(String packageName) {
    List<String> logLevelList = new ArrayList<>();
    if (packageName == null) {
      LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
      List<Logger> loggerList = loggerContext.getLoggerList();
      for (Logger logger : loggerList) {
        if (logger.getLevel() != null) {
          logLevelList.add(logger.getName() + ":" +  logger.getLevel().toString());
        }
      }
    } else {
      LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
      Logger logger = loggerContext.getLogger(packageName);
      Level logLevel = logger.getLevel();
      if (logLevel != null) {
        logLevelList.add(packageName + ":" +  logLevel.toString());
      } else {
        logLevelList.add(packageName + ": Log level not set for this package");
      }
    }
    return logLevelList;
  }

  /**
   * Validate input log level.
   */
  private void validateLogLevel(String level) {
    if (!LOG_LEVELS.contains(level)) {
      throw new KameHouseBadRequestException("Invalid log level " + level);
    }
  }
}
