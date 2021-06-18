package com.nicobrest.kamehouse.admin.model;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;
import com.nicobrest.kamehouse.admin.model.systemcommand.ShutdownSystemCommand;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test for the ShutdownSystemCommand.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesUtils.class })
public class ShutdownSystemCommandTest {

  @Before
  public void before() {
    PowerMockito.mockStatic(PropertiesUtils.class);
  }

  @Test
  public void shutdownSystemCommandWindowsTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);

    ShutdownSystemCommand command = new ShutdownSystemCommand(180);
    assertEquals(0, command.getSleepTime());
    assertEquals("[cmd.exe, /c, start, shutdown, /s, /t , 180]",
        command.getCommand().toString());
  }

  @Test
  public void shutdownSystemCommandLinuxTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);

    ShutdownSystemCommand command = new ShutdownSystemCommand(55);
    assertEquals(0, command.getSleepTime());
    assertEquals("[/bin/bash, -c, sudo /sbin/shutdown -P , 0]",
        command.getCommand().toString());
  }
}
