package com.nicobrest.kamehouse.admin.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownSystemCommand;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Test for the ShutdownSystemCommand. */
class ShutdownSystemCommandTest {

  private MockedStatic<PropertiesUtils> propertiesUtils;

  @BeforeEach
  public void before() {
    MockitoAnnotations.openMocks(this);
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
  }

  @AfterEach
  public void close() {
    propertiesUtils.close();
  }

  @Test
  void shutdownSystemCommandWindowsTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);

    ShutdownSystemCommand command = new ShutdownSystemCommand(180);
    assertEquals(0, command.getSleepTime());
    assertEquals("[cmd.exe, /c, start, shutdown, /s, /t , 180]", command.getCommand().toString());
  }

  @Test
  void shutdownSystemCommandLinuxTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    when(PropertiesUtils.getUserHome()).thenReturn(null);

    ShutdownSystemCommand command = new ShutdownSystemCommand(55);
    assertEquals(0, command.getSleepTime());
    String outputCommand = command.getCommand().toString();
    assertNotNull(outputCommand);
    assertTrue(
        outputCommand.contains("/programs/kamehouse-shell/bin/lin/shutdown/shutdown.sh -d 0]"));
  }
}
