package com.nicobrest.kamehouse.systemcommand.service;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

import com.nicobrest.kamehouse.admin.model.AdminShutdownCommand;
import com.nicobrest.kamehouse.admin.model.AdminVlcCommand;
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
  
  @Test
  public void executeTest() throws Exception {
    String inputStreamString = "/home /bin /opt";
    InputStream processInputStream = new ByteArrayInputStream(inputStreamString.getBytes());
    InputStream processErrorStream = new ByteArrayInputStream("".getBytes());
    when(ProcessUtils.getInputStreamFromProcess(Mockito.any())).thenReturn(processInputStream); 
    when(ProcessUtils.getErrorStreamFromProcess(Mockito.any())).thenReturn(processErrorStream);
    
	  SystemCommand systemCommand = new SystemCommand();
	  systemCommand.setCommand(Arrays.asList("ls"));
	  
	  SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);
	  
	  assertEquals("[ls]", systemCommandOutput.getCommand());
	  assertEquals("completed", systemCommandOutput.getStatus());
	  assertEquals(-1, systemCommandOutput.getPid());
	  assertEquals(0, systemCommandOutput.getExitCode());
	  assertEquals(inputStreamString, systemCommandOutput.getStandardOutput().get(0));
	  assertEquals(new ArrayList<String>(), systemCommandOutput.getStandardError());
  }
  
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

  @Test
  public void getSystemCommandsShutdownSetLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_LINUX.get());
    expectedSystemCommand.add("90");

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.SET);
    adminShutdownCommand.setTime(5400);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsShutdownSetWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_WINDOWS.get());
    expectedSystemCommand.add("5400");

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.SET);
    adminShutdownCommand.setTime(5400);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsShutdownSetExceptionTest() {

    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.SET);

    thrown.expect(KameHouseInvalidCommandException.class);

    systemCommandService.getSystemCommands(adminShutdownCommand);
  }

  @Test
  public void getSystemCommandsShutdownCancelLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_CANCEL_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.CANCEL);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsShutdownCancelWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_CANCEL_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.CANCEL);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsShutdownStatusLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_STATUS_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsShutdownStatusWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.SHUTDOWN_STATUS_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminShutdownCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsVlcStartLinuxTest() {

    List<String> expectedSystemCommand1 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand1, CommandLine.VLC_STOP_LINUX.get());
    List<String> expectedSystemCommand2 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand2, CommandLine.VLC_START_LINUX.get());
    expectedSystemCommand2.add("D:\\marvel.m3u");

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.START);
    adminVlcCommand.setFile("D:\\marvel.m3u");

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(2, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand1, returnedSystemCommands.get(0).getCommand());
    assertEquals(expectedSystemCommand2, returnedSystemCommands.get(1).getCommand());
  }

  @Test
  public void getSystemCommandsVlcStartWindowsTest() {

    List<String> expectedSystemCommand1 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand1, CommandLine.VLC_STOP_WINDOWS.get());
    List<String> expectedSystemCommand2 = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand2, CommandLine.VLC_START_WINDOWS.get());
    expectedSystemCommand2.add("D:\\marvel.m3u");

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.START);
    adminVlcCommand.setFile("D:\\marvel.m3u");

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(2, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand1, returnedSystemCommands.get(0).getCommand());
    assertEquals(expectedSystemCommand2, returnedSystemCommands.get(1).getCommand());
  }

  @Test
  public void getSystemCommandsVlcStopLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STOP_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.STOP);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsVlcStoptWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STOP_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.STOP);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsVlcStatusLinuxTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STATUS_LINUX.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }

  @Test
  public void getSystemCommandsVlcStatustWindowsTest() {

    List<String> expectedSystemCommand = new ArrayList<String>();
    Collections.addAll(expectedSystemCommand, CommandLine.VLC_STATUS_WINDOWS.get());

    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.STATUS);

    List<SystemCommand> returnedSystemCommands = systemCommandService.getSystemCommands(
        adminVlcCommand);

    assertEquals(1, returnedSystemCommands.size());
    assertEquals(expectedSystemCommand, returnedSystemCommands.get(0).getCommand());
  }
}
