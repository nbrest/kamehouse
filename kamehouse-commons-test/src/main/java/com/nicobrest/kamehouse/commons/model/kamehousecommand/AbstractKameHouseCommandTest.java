package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test for the KameHouseCommands.
 */
public abstract class AbstractKameHouseCommandTest {

  protected static final String HOME_WIN = "C:\\Users\\goku";

  private MockedStatic<PropertiesUtils> propertiesUtils;

  /**
   * Resets mock objects.
   */
  @BeforeEach
  void before() {
    MockitoAnnotations.openMocks(this);
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    when(PropertiesUtils.getUserHome()).thenReturn("/home/goku");
  }

  /**
   * Resets mock objects.
   */
  @AfterEach
  void close() {
    propertiesUtils.close();
  }

  /**
   * Linux command test.
   */
  @Test
  void windowsTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(PropertiesUtils.getUserHome()).thenReturn(HOME_WIN);
    var kameHouseCommand = getKameHouseCommand();
    kameHouseCommand.init();

    String outputCommand = kameHouseCommand.getCommand();

    assertEquals(getExpectedWindowsCommmand(), outputCommand);
  }

  /**
   * Windows command test.
   */
  @Test
  void linuxTest() {
    var kameHouseCommand = getKameHouseCommand();
    kameHouseCommand.init();
    String outputCommand = kameHouseCommand.getCommand();

    assertEquals(getExpectedLinuxCommmand(), outputCommand);
  }

  /**
   * KameHouse command to test.
   */
  protected abstract KameHouseCommand getKameHouseCommand();

  /**
   * Windows script to expect with parameters.
   */
  protected abstract String getWindowsShellCommand();

  /**
   * Linux script to expect with parameters.
   */
  protected abstract String getLinuxShellCommand();

  /**
   * Full windows command to expect.
   */
  protected String getExpectedWindowsCommmand() {
    return "C:\\Users\\goku/programs/kamehouse-shell/bin/win/bat/git-bash.bat "
        + "-c \"${HOME}/programs/kamehouse-shell/bin/" + getWindowsShellCommand() + "\"";
  }

  /**
   * Full linux command to expect.
   */
  protected String getExpectedLinuxCommmand() {
    return "/bin/bash -c /home/goku/programs/kamehouse-shell/bin/" + getLinuxShellCommand();
  }
}
