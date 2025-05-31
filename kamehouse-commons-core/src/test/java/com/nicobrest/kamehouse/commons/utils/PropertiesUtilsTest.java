package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * PropertiesUtils tests. NOTE: I wouldn't test this class with it's current version (2019/05/19).
 * But * adding the tests for practice.
 *
 * @author nbrest
 */
class PropertiesUtilsTest {

  private MockedStatic<PropertiesUtils> propertiesUtils;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void before() {
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
    when(PropertiesUtils.getProperty(any())).thenCallRealMethod();
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  public void close() {
    propertiesUtils.close();
  }

  /**
   * getProperty test.
   */
  @Test
  void getPropertyTest() {
    String expectedPropertyValue = "mada mada dane";

    String returnedPropertyValue = PropertiesUtils.getProperty("my.test.property");

    assertEquals(expectedPropertyValue, returnedPropertyValue);
  }

  /**
   * Test getting the home directory.
   */
  @Test
  void getUserHomeTest() {
    when(PropertiesUtils.getUserHome()).thenCallRealMethod();

    String home = PropertiesUtils.getUserHome();

    assertNotNull(home);
  }

  /**
   * Test getting the hostname in a windows server.
   */
  @Test
  void getHostnameWindowsTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(PropertiesUtils.getHostname()).thenCallRealMethod();

    String hostname = PropertiesUtils.getHostname();

    assertNotNull(hostname);
  }

  /**
   * Test getting the hostname in a linux server.
   */
  @Test
  void getHostnameLinuxTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    when(PropertiesUtils.getHostname()).thenCallRealMethod();

    String hostname = PropertiesUtils.getHostname();

    assertNotNull(hostname);
  }

  /**
   * Test getting the module name.
   */
  @Test
  void getModuleNameTest() {
    when(PropertiesUtils.getModuleName()).thenCallRealMethod();

    String moduleName = PropertiesUtils.getModuleName();

    assertNotNull(moduleName);
  }

  /**
   * Get default value test.
   */
  @Test
  void getPropertyDefaultValueTest() {
    when(PropertiesUtils.getProperty(any(), any())).thenCallRealMethod();

    String value = PropertiesUtils.getProperty("", "DEFAULT_MADA");

    assertEquals("DEFAULT_MADA", value);
  }

  /**
   * Get boolean property test.
   */
  @Test
  void getBooleanPropertyTest() {
    when(PropertiesUtils.getBooleanProperty(any())).thenCallRealMethod();
    PropertiesUtils.loadBuildVersion();
    PropertiesUtils.loadBuildDate();
    PropertiesUtils.loadGitCommitHash();

    boolean value = PropertiesUtils.getBooleanProperty("invalid-property");

    assertFalse(value);
  }
}
