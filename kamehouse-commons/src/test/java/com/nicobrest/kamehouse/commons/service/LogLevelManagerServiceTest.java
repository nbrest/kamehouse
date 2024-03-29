package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.web.filter.logger.CustomRequestLoggingFilter;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Unit tests for the LogLevelManagerService class.
 *
 * @author nbrest
 */
class LogLevelManagerServiceTest {

  private LogLevelManagerService logLevelManagerService;

  @Mock
  private CustomRequestLoggingFilter customRequestLoggingFilter;

  /**
   * Resets mock objects.
   */
  @BeforeEach
  void beforeTest() {
    logLevelManagerService = new LogLevelManagerService(customRequestLoggingFilter);
  }

  /**
   * Tests validating the specified log level.
   */
  @Test
  void validateLogLevelSuccessfulTest() {
    Assertions.assertDoesNotThrow(() -> {
      logLevelManagerService.validateLogLevel("TRACE");
    });
  }

  /**
   * Tests validating an invalid log level.
   */
  @Test
  void validateLogLevelInvalidLevelTest() {
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          logLevelManagerService.validateLogLevel("TRACEs");
        });
  }

  /**
   * Tests getting the log level for the specified package.
   */
  @Test
  void getLogLevelSuccessfulTest() {
    List<String> logLevel = logLevelManagerService.getLogLevel("com.nicobrest.kamehouse");
    assertEquals(1, logLevel.size());
    assertEquals("com.nicobrest.kamehouse:INFO", logLevel.get(0));
  }

  /**
   * Tests getting the log level for all packages that have it set.
   */
  @Test
  void getLogLevelAllPackagesSuccessfulTest() {
    List<String> logLevel = logLevelManagerService.getLogLevel(null);
    assertEquals(11, logLevel.size());
  }

  /**
   * Tests getting the log level for an invalid package.
   */
  @Test
  void getLogLevelInvalidPackageTest() {
    List<String> logLevel = logLevelManagerService.getLogLevel("com.invalid.package");
    assertEquals(1, logLevel.size());
    assertEquals("com.invalid.package: Log level not set for this package", logLevel.get(0));
  }

  /**
   * Tests setting the specified log level.
   */
  @Test
  void setLogLevelSuccessfulTest() {
    logLevelManagerService.setLogLevel("WARN", "com.nicobrest.kamehouse");
    List<String> logLevel = logLevelManagerService.getLogLevel("com.nicobrest.kamehouse");
    assertEquals(1, logLevel.size());
    assertEquals("com.nicobrest.kamehouse:WARN", logLevel.get(0));
  }

  /**
   * Tests resetting the log levels to the default values.
   */
  @Test
  void resetLogLevelsSuccessfulTest() {
    logLevelManagerService.resetLogLevels();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:INFO", logLevels.get(1));
  }

  /**
   * Tests setting kamehouse log levels to TRACE.
   */
  @Test
  void setKamehouseLogLevelsToTraceSuccessfulTest() {
    logLevelManagerService.setKamehouseLogLevelsToTrace();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:TRACE", logLevels.get(1));
  }

  /**
   * Tests setting kamehouse log levels to DEBUG.
   */
  @Test
  void setKamehouseLogLevelsToDebugSuccessfulTest() {
    logLevelManagerService.setKamehouseLogLevelsToDebug();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:DEBUG", logLevels.get(1));
  }

  /**
   * Tests setting kamehouse log levels to INFO.
   */
  @Test
  void setKamehouseLogLevelsToInfoSuccessfulTest() {
    logLevelManagerService.setKamehouseLogLevelsToInfo();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:INFO", logLevels.get(1));
  }
}
