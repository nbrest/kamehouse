package com.nicobrest.kamehouse.commons.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.web.filter.logger.CustomRequestLoggingFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to control the log level of kamehouse.
 */
@Service
public class LogLevelManagerService {

  private static final String INFO = "INFO";
  private static final String DEBUG = "DEBUG";
  private static final String WARN = "WARN";
  private static final List<String> LOG_LEVELS =
      Collections.unmodifiableList(Arrays.asList("ERROR", WARN, INFO, DEBUG, "TRACE"));

  private static final String MODULE_NAME = PropertiesUtils.getModuleName();
  private static final String ROOT_KAMEHOUSE_PACKAGE = "com.nicobrest.kamehouse";

  protected static final Map<String, String> KAMEHOUSE_PACKAGES_LOG_LEVEL;
  protected static final Map<String, String> EXTERNAL_PACKAGES_LOG_LEVEL;

  @Autowired
  CustomRequestLoggingFilter customRequestLoggingFilter;

  static {
    Map<String, String> kamehousePackages = new HashMap<>();
    kamehousePackages.put(ROOT_KAMEHOUSE_PACKAGE, INFO);
    kamehousePackages.put("com.nicobrest.kamehouse.commons", INFO);
    kamehousePackages.put(
        "com.nicobrest.kamehouse.commons.controller.LogLevelManagerController", INFO);
    kamehousePackages.put(
        "com.nicobrest.kamehouse.commons.web.filter.logger" + ".CustomRequestLoggingFilter",
        DEBUG);
    KAMEHOUSE_PACKAGES_LOG_LEVEL = Collections.unmodifiableMap(kamehousePackages);

    Map<String, String> externalPackages = new HashMap<>();
    externalPackages.put("org.springframework", INFO);
    externalPackages.put("org.springframework.security", INFO);
    externalPackages.put(
        "org.springframework.web.socket.config.WebSocketMessageBrokerStats", WARN);
    externalPackages.put("org.hibernate.hql.internal.QueryTranslatorFactoryInitiator", WARN);
    EXTERNAL_PACKAGES_LOG_LEVEL = Collections.unmodifiableMap(externalPackages);
  }

  /**
   * Set include/exclude payload in request logging.
   */
  public void setIncludePayload(boolean logPayload) {
    customRequestLoggingFilter.setIncludePayload(logPayload);
  }

  /**
   * Set include/exclude headers in request logging.
   */
  public void setIncludeHeaders(boolean logHeaders) {
    customRequestLoggingFilter.setIncludeHeaders(logHeaders);
  }

  /**
   * Set include/exclude client info in request logging.
   */
  public void setIncludeClientInfo(boolean logClientInfo) {
    customRequestLoggingFilter.setIncludeClientInfo(logClientInfo);
  }

  /**
   * Set include/exclude query string in request logging.
   */
  public void setIncludeQueryString(boolean logQueryString) {
    customRequestLoggingFilter.setIncludeQueryString(logQueryString);
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
          logLevelList.add(logger.getName() + ":" + logger.getLevel().toString());
        }
      }
    } else {
      Logger logger = loggerContext.getLogger(packageName);
      Level logLevel = logger.getLevel();
      if (logLevel != null) {
        logLevelList.add(packageName + ":" + logLevel.toString());
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
    setLogLevel(INFO, getModulePackage());

    for (Map.Entry<String, String> entry : EXTERNAL_PACKAGES_LOG_LEVEL.entrySet()) {
      setLogLevel(entry.getValue(), entry.getKey());
    }
  }

  /**
   * Set kamehouse log levels to INFO.
   */
  public void setKamehouseLogLevelsToInfo() {
    resetLogLevels();
    setKamehouseLogLevels(INFO);
  }

  /**
   * Set kamehouse log levels to DEBUG.
   */
  public void setKamehouseLogLevelsToDebug() {
    resetLogLevels();
    setKamehouseLogLevels(DEBUG);
  }

  /**
   * Set kamehouse log levels to TRACE.
   */
  public void setKamehouseLogLevelsToTrace() {
    resetLogLevels();
    setKamehouseLogLevels("TRACE");
  }

  /**
   * Gets the package name for the current module.
   */
  private static String getModulePackage() {
    return ROOT_KAMEHOUSE_PACKAGE + "." + MODULE_NAME;
  }

  /**
   * Set kamehouse log levels to the specified log level.
   */
  private void setKamehouseLogLevels(String logLevel) {
    for (String packageName : KAMEHOUSE_PACKAGES_LOG_LEVEL.keySet()) {
      setLogLevel(logLevel, packageName);
    }
    setLogLevel(logLevel, getModulePackage());
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
