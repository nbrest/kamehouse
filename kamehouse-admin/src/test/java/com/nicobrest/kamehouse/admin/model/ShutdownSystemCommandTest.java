package com.nicobrest.kamehouse.admin.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class ShutdownSystemCommandTest {

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
  public void shutdownSystemCommandWindowsTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);

    ShutdownSystemCommand command = new ShutdownSystemCommand(180);
    assertEquals(0, command.getSleepTime());
    assertEquals("[cmd.exe, /c, start, shutdown, /s, /t , 180]", command.getCommand().toString());
  }

  @Test
  public void shutdownSystemCommandLinuxTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);

    ShutdownSystemCommand command = new ShutdownSystemCommand(55);
    assertEquals(0, command.getSleepTime());
    assertEquals("[/bin/bash, -c, sudo /sbin/shutdown -P , 0]", command.getCommand().toString());
  }
}
