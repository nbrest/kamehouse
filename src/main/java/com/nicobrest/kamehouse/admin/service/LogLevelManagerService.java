package com.nicobrest.kamehouse.admin.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service to control the log level of kamehouse.
 */
public class LogLevelManagerService {

  private static final List<String> LOG_LEVELS =
      Arrays.asList("ERROR", "WARN", "INFO", "DEBUG", "TRACE");

  /**
   * Set the log level for the specified package.
   */
  public void setLogLevel(String level, String packageName) {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger logger = loggerContext.getLogger(packageName);
    logger.setLevel(Level.toLevel(level));
  }

  /**
   * Get the current log level for the specified package.
   */
  public List<String> getLogLevel(String packageName) {
    List<String> logLevelList = new ArrayList<>();
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    if (packageName == null) {
      List<Logger> loggerList = loggerContext.getLoggerList();
      for (Logger logger : loggerList) {
        if (logger.getLevel() != null) {
          logLevelList.add(logger.getName() + ":" +  logger.getLevel().toString());
        }
      }
    } else {
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
  public void validateLogLevel(String level) {
    if (!LOG_LEVELS.contains(level)) {
      throw new KameHouseBadRequestException("Invalid log level " + level);
    }
  }
}
