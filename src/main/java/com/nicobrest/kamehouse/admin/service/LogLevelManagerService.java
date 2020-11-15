package com.nicobrest.kamehouse.admin.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to control the log level of kamehouse.
 */
public class LogLevelManagerService {

  private static final List<String> LOG_LEVELS =
      Arrays.asList("ERROR", "WARN", "INFO", "DEBUG", "TRACE");

  private static Map<String, String> KAMEHOUSE_PACKAGES_LOG_LEVEL;
  private static Map<String, String> EXTERNAL_PACKAGES_LOG_LEVEL;

  static {
    KAMEHOUSE_PACKAGES_LOG_LEVEL = new HashMap<>();
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse","INFO");
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse.admin","INFO");
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse.main","INFO");
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse.media","INFO");
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse.tennisworld","INFO");
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse.testmodule","INFO");
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse.vlcrc","INFO");
    KAMEHOUSE_PACKAGES_LOG_LEVEL.put("com.nicobrest.kamehouse.admin.controller"
        + ".LogLevelManagerController","INFO");

    EXTERNAL_PACKAGES_LOG_LEVEL = new HashMap<>();
    EXTERNAL_PACKAGES_LOG_LEVEL.put("org.springframework","INFO");
    EXTERNAL_PACKAGES_LOG_LEVEL.put("org.springframework.security","INFO");
    EXTERNAL_PACKAGES_LOG_LEVEL.put("org.springframework.web.socket.config"
        + ".WebSocketMessageBrokerStats","WARN");
    EXTERNAL_PACKAGES_LOG_LEVEL.put("org.hibernate.hql.internal.QueryTranslatorFactoryInitiator",
        "WARN");
  }

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

  /**
   * Reset all log levels to the default values.
   */
  public void resetLogLevels() {
    clearLogLevels();

    for (Map.Entry<String, String> entry : KAMEHOUSE_PACKAGES_LOG_LEVEL.entrySet()) {
      setLogLevel(entry.getValue(), entry.getKey());
    }

    for (Map.Entry<String, String> entry  : EXTERNAL_PACKAGES_LOG_LEVEL.entrySet()) {
      setLogLevel(entry.getValue(), entry.getKey());
    }
  }

  /**
   * Set kamehouse log levels to TRACE.
   */
  public void setKamehouseLogLevelsToTrace() {
    resetLogLevels();

    for (String packageName : KAMEHOUSE_PACKAGES_LOG_LEVEL.keySet()) {
      setLogLevel("TRACE", packageName);
    }
  }

  /**
   * Clear the log levels of all packages.
   */
  private void clearLogLevels() {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    List<Logger> loggerList = loggerContext.getLoggerList();
    for (Logger logger : loggerList) {
      if (logger.getLevel() != null && !isRootLogger(logger)) {
        logger.setLevel(null);
      }
    }
  }

  /**
   * Returns true if it's the ROOT logger.
   */
  private boolean isRootLogger(Logger logger) {
    return "ROOT".equals(logger.getName());
  }
}
