package com.nicobrest.kamehouse.systemcommand.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.when;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.CommandLine;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.utils.ProcessUtils;
import com.nicobrest.kamehouse.utils.PropertiesUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for the SystemCommandService class. If this class becomes too big,
 * split it into separate test classes. For example, one test class for the
 * execute methods, another for shutdown commands, another for vlc commands,
 * another for lock/unlock screen, etc.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesUtils.class, ProcessUtils.class })
public class SystemCommandServiceTest {

  private SystemCommandService systemCommandService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() {
    PowerMockito.mockStatic(PropertiesUtils.class, ProcessUtils.class);
    systemCommandService = PowerMockito.spy(new SystemCommandService());
  }

  /**
   * Execute process successful test.
   */
  @Test
  public void executeTest() throws Exception {
    String inputStreamString = "/home /bin /opt";
    InputStream processInputStream = new ByteArrayInputStream(inputStreamString.getBytes());
    InputStream processErrorStream = new ByteArrayInputStream("".getBytes());
    when(ProcessUtils.getInputStreamFromProcess(Mockito.any())).thenReturn(processInputStream);
    when(ProcessUtils.getErrorStreamFromProcess(Mockito.any())).thenReturn(processErrorStream);

    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("ls"));
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    systemCommands.add(systemCommand);
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);

    assertEquals("[ls]", systemCommandOutputs.get(0).getCommand());
    assertEquals("completed", systemCommandOutputs.get(0).getStatus());
    assertEquals(-1, systemCommandOutputs.get(0).getPid());
    assertEquals(0, systemCommandOutputs.get(0).getExitCode());
    assertEquals(inputStreamString, systemCommandOutputs.get(0).getStandardOutput().get(0));
    assertEquals(new ArrayList<String>(), systemCommandOutputs.get(0).getStandardError());
  }

  /**
   * Execute daemon process successful test.
   */
  @Test
  public void executeDaemonTest() throws Exception {
    InputStream processInputStream = new ByteArrayInputStream("".getBytes());
    InputStream processErrorStream = new ByteArrayInputStream("".getBytes());
    when(ProcessUtils.getInputStreamFromProcess(Mockito.any())).thenReturn(processInputStream);
    when(ProcessUtils.getErrorStreamFromProcess(Mockito.any())).thenReturn(processErrorStream);

    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("vlc"));
    systemCommand.setIsDaemon(true);

    SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);

    assertEquals("[vlc]", systemCommandOutput.getCommand());
    assertEquals("running", systemCommandOutput.getStatus());
    assertEquals(-1, systemCommandOutput.getPid());
    assertEquals(-1, systemCommandOutput.getExitCode());
    assertEquals(null, systemCommandOutput.getStandardOutput());
    assertEquals(null, systemCommandOutput.getStandardError());
  }

  /**
   * Get system commands exception test.
   */
  @Test
  public void getSystemCommandsExceptionTest() {

    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand("INVALID COMMAND");

    thrown.expect(KameHouseInvalidCommandException.class);

    systemCommandService.getSystemCommands(adminShutdownCommand);

    fail("Should have thrown exception");
  }

  /**
   * Get set shutdown system commands linux successful test.
   */
  @Test
  public void getSystemCommandsShutdownSetLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_LINUX.get());
    expectedSystemCommand.add("90");

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_SET);
    adminShutdownCommand.setTime(5400);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get set shutdown system commands windows successful test.
   */
  @Test
  public void getSystemCommandsShutdownSetWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_WINDOWS.get());
    expectedSystemCommand.add("5400");

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_SET);
    adminShutdownCommand.setTime(5400);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get set shutdown system commands exception test. Invalid time.
   */
  @Test
  public void getSystemCommandsSetShutdownExceptionTest() {

    AdminCommand adminShutdownCommand = new AdminCommand(AdminCommand.SHUTDOWN_SET);
    adminShutdownCommand.setTime(-1);

    thrown.expect(KameHouseInvalidCommandException.class);

    systemCommandService.getSystemCommands(adminShutdownCommand);

    fail("Should have thrown exception");
  }

  /**
   * Get cancel shutdown system commands linux successful test.
   */
  @Test
  public void getSystemCommandsShutdownCancelLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_CANCEL_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_CANCEL);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get cancel shutdown system commands windows successful test.
   */
  @Test
  public void getSystemCommandsShutdownCancelWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_CANCEL_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_CANCEL);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get status shutdown system commands linux successful test.
   */
  @Test
  public void getSystemCommandsShutdownStatusLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_STATUS_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get status shutdown system commands windows successful test.
   */
  @Test
  public void getSystemCommandsShutdownStatusWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_STATUS_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get suspend system commands linux successful test.
   */
  @Test
  public void getSystemCommandsSuspendLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SUSPEND_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SUSPEND);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get suspend system commands windows successful test.
   */
  @Test
  public void getSystemCommandsSuspendWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SUSPEND_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SUSPEND);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get start vlc system commands linux successful test.
   */
  @Test
  public void getSystemCommandsVlcStartLinuxTest() {

    List<String> expectedSystemCommand1 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand1, CommandLine.VLC_STOP_LINUX.get());
    List<String> expectedSystemCommand2 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand2, CommandLine.VLC_START_LINUX.get());
    expectedSystemCommand2.add("D:\\marvel.m3u");

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminVlcCommand = new AdminCommand();
    adminVlcCommand.setCommand(AdminCommand.VLC_START);
    adminVlcCommand.setFile("D:\\marvel.m3u");

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(2, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand1, returnedSystemCommands.get(0).getCommand());
    assertEquals(expectedSystemCommand2, returnedSystemCommands.get(1).getCommand());
  }

  /**
   * Get start vlc system commands windows successful test.
   */
  @Test
  public void getSystemCommandsVlcStartWindowsTest() {

    List<String> expectedSystemCommand1 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand1, CommandLine.VLC_STOP_WINDOWS.get());
    List<String> expectedSystemCommand2 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand2, CommandLine.VLC_START_WINDOWS.get());
    expectedSystemCommand2.add("D:\\marvel.m3u");

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminVlcCommand = new AdminCommand();
    adminVlcCommand.setCommand(AdminCommand.VLC_START);
    adminVlcCommand.setFile("D:\\marvel.m3u");

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(2, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand1, returnedSystemCommands.get(0).getCommand());
    assertEquals(expectedSystemCommand2, returnedSystemCommands.get(1).getCommand());
  }

  /**
   * Get stop vlc system commands linux successful test.
   */
  @Test
  public void getSystemCommandsVlcStopLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STOP_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminVlcCommand = new AdminCommand();
    adminVlcCommand.setCommand(AdminCommand.VLC_STOP);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get stop vlc system commands windows successful test.
   */
  @Test
  public void getSystemCommandsVlcStoptWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STOP_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminVlcCommand = new AdminCommand();
    adminVlcCommand.setCommand(AdminCommand.VLC_STOP);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get status vlc system commands linux successful test.
   */
  @Test
  public void getSystemCommandsVlcStatusLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STATUS_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminVlcCommand = new AdminCommand();
    adminVlcCommand.setCommand(AdminCommand.VLC_STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get status vlc system commands windows successful test.
   */
  @Test
  public void getSystemCommandsVlcStatustWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STATUS_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminVlcCommand = new AdminCommand();
    adminVlcCommand.setCommand(AdminCommand.VLC_STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get lock screen system commands linux successful test.
   */
  @Test
  public void getSystemCommandsLockScreenLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.LOCK_SCREEN_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SCREEN_LOCK);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get lock screen system commands windows successful test.
   */
  @Test
  public void getSystemCommandsLockScreenWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.LOCK_SCREEN_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SCREEN_LOCK);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get unlock screen system commands linux successful test.
   */
  @Test
  public void getSystemCommandsUnlockScreenLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.LOCK_SCREEN_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    when(PropertiesUtils.getHostname()).thenReturn("planet-vegita");
    when(PropertiesUtils.getUserHome()).thenReturn("src/test/resources");
    when(PropertiesUtils.getAdminProperty("unlock.screen.pwd.file")).thenReturn(
        "admin/pwds/unlock.screen.pwd");
    when(PropertiesUtils.getAdminProperty("vnc.server.pwd.file")).thenReturn(
        "admin/pwds/vnc.server.pwd");
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SCREEN_UNLOCK);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(4, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get unlock screen system commands windows successful test.
   */
  @Test
  public void getSystemCommandsUnlockScreenWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.LOCK_SCREEN_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SCREEN_UNLOCK);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(4, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  /**
   * Get wake up screen system commands linux successful test.
   */
  @Test
  public void getSystemCommandsWakeUpScreenLinuxTest() {

    String expectedSystemCommand = "[/bin/bash, -c, /usr/local/bin/vncdo --server planet-vegita"
        + " --password '' move 400 400 click 1]";

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    when(PropertiesUtils.getHostname()).thenReturn("planet-vegita");
    when(PropertiesUtils.getUserHome()).thenReturn("src/test/resources");
    when(PropertiesUtils.getAdminProperty("vnc.server.pwd.file")).thenReturn(
        "admin/pwds/vnc.server.pwd");
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SCREEN_WAKE_UP);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(3, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand().toString());
  }

  /**
   * Get wake up screen system commands windows successful test.
   */
  @Test
  public void getSystemCommandsWakeUpScreenWindowsTest() {

    String expectedSystemCommand = "[cmd.exe, /c, vncdo, --server, null, --password,"
        + " ERROR_READING_PASSWORD, move, 400, 400, click, 1]";

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminCommand adminCommand = new AdminCommand(AdminCommand.SCREEN_WAKE_UP);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminCommand);

    assertEquals(3, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand().toString());
  }
}
