package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the LogLevelManagerService class.
 *
 * @author nbrest
 */
public class LogLevelManagerServiceTest {

  private LogLevelManagerService logLevelManagerService;

  /** Resets mock objects. */
  @BeforeEach
  public void beforeTest() {
    logLevelManagerService = new LogLevelManagerService();
  }

  /** Tests validating the specified log level. */
  @Test
  public void validateLogLevelSuccessfulTest() {
    logLevelManagerService.validateLogLevel("TRACE");
    // expect no exception thrown.
  }

  /** Tests validating an invalid log level. */
  @Test
  public void validateLogLevelInvalidLevelTest() {
    assertThrows(
        KameHouseBadRequestException.class,
        () -> {
          logLevelManagerService.validateLogLevel("TRACEs");
        });
  }

  /** Tests getting the log level for the specified package. */
  @Test
  public void getLogLevelSuccessfulTest() {
    List<String> logLevel = logLevelManagerService.getLogLevel("com.nicobrest.kamehouse");
    assertEquals(1, logLevel.size());
    assertEquals("com.nicobrest.kamehouse:INFO", logLevel.get(0));
  }

  /** Tests getting the log level for all packages that have it set. */
  @Test
  public void getLogLevelAllPackagesSuccessfulTest() {
    List<String> logLevel = logLevelManagerService.getLogLevel(null);
    assertEquals(10, logLevel.size());
  }

  /** Tests getting the log level for an invalid package. */
  @Test
  public void getLogLevelInvalidPackageTest() {
    List<String> logLevel = logLevelManagerService.getLogLevel("com.invalid.package");
    assertEquals(1, logLevel.size());
    assertEquals("com.invalid.package: Log level not set for this package", logLevel.get(0));
  }

  /** Tests setting the specified log level. */
  @Test
  public void setLogLevelSuccessfulTest() {
    logLevelManagerService.setLogLevel("WARN", "com.nicobrest.kamehouse");
    List<String> logLevel = logLevelManagerService.getLogLevel("com.nicobrest.kamehouse");
    assertEquals(1, logLevel.size());
    assertEquals("com.nicobrest.kamehouse:WARN", logLevel.get(0));
  }

  /** Tests resetting the log levels to the default values. */
  @Test
  public void resetLogLevelsSuccessfulTest() {
    logLevelManagerService.resetLogLevels();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:INFO", logLevels.get(1));
  }

  /** Tests setting kamehouse log levels to TRACE. */
  @Test
  public void setKamehouseLogLevelsToTraceSuccessfulTest() {
    logLevelManagerService.setKamehouseLogLevelsToTrace();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:TRACE", logLevels.get(1));
  }

  /** Tests setting kamehouse log levels to DEBUG. */
  @Test
  public void setKamehouseLogLevelsToDebugSuccessfulTest() {
    logLevelManagerService.setKamehouseLogLevelsToDebug();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:DEBUG", logLevels.get(1));
  }

  /** Tests setting kamehouse log levels to INFO. */
  @Test
  public void setKamehouseLogLevelsToInfoSuccessfulTest() {
    logLevelManagerService.setKamehouseLogLevelsToInfo();

    List<String> logLevels = logLevelManagerService.getLogLevel(null);
    assertEquals(
        LogLevelManagerService.KAMEHOUSE_PACKAGES_LOG_LEVEL.size()
            + LogLevelManagerService.EXTERNAL_PACKAGES_LOG_LEVEL.size(),
        logLevels.size() - 2);
    assertEquals("com.nicobrest.kamehouse:INFO", logLevels.get(1));
  }
}
