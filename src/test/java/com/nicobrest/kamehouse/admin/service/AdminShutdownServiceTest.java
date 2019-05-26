package com.nicobrest.kamehouse.admin.service;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;

import com.nicobrest.kamehouse.admin.model.AdminShutdownCommand;
import com.nicobrest.kamehouse.main.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminShutdownServiceTest {

  @InjectMocks
  private AdminShutdownService adminShutdownService;

  @Mock
  private SystemCommandService systemCommandService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(systemCommandService);
  }

  @Test
  public void setShutdownTest() {
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand(AdminShutdownCommand.SET);
    adminShutdownCommand.setTime(5400);
    List<SystemCommand> systemCommands = mockSetShutdownSystemCommands();
    when(systemCommandService.getSystemCommands(adminShutdownCommand)).thenReturn(systemCommands);
    SystemCommandOutput systemCommandOutput = mockSetShutdownCommandOutput();
    when(systemCommandService.execute(any())).thenReturn(systemCommandOutput);
    
    List<SystemCommandOutput> systemCommandOutputs = adminShutdownService.setShutdown(
        adminShutdownCommand);
    
    assertEquals(1, systemCommandOutputs.size());
    assertEquals(systemCommandOutput, systemCommandOutputs.get(0));
  }

  @Test
  public void setShutdownInvalidCommandTest() {
    thrown.expect(KameHouseInvalidCommandException.class);
    thrown.expectMessage("Invalid AdminShutdownCommand ");
    
    AdminShutdownCommand adminShutdownCommand = new AdminShutdownCommand();
    adminShutdownCommand.setCommand("InvalidCommand");
    adminShutdownCommand.setTime(5400);
    
    adminShutdownService.setShutdown(adminShutdownCommand);
  }

  @Test
  public void cancelShutdownTest() {
    List<SystemCommand> systemCommands = mockCancelShutdownSystemCommands();
    when(systemCommandService.getSystemCommands(any(AdminShutdownCommand.class))).thenReturn(systemCommands);
    SystemCommandOutput systemCommandOutput = mockCancelShutdownCommandOutput();
    when(systemCommandService.execute(any())).thenReturn(systemCommandOutput);
    
    List<SystemCommandOutput> systemCommandOutputs = adminShutdownService.cancelShutdown();
    
    assertEquals(1, systemCommandOutputs.size());
    assertEquals(systemCommandOutput, systemCommandOutputs.get(0));
  }

  @Test
  public void statusShutdownTest() {
    List<SystemCommand> systemCommands = mockStatusShutdownSystemCommands();
    when(systemCommandService.getSystemCommands(any(AdminShutdownCommand.class))).thenReturn(systemCommands);
    SystemCommandOutput systemCommandOutput = mockStatusShutdownCommandOutput();
    when(systemCommandService.execute(any())).thenReturn(systemCommandOutput);
    
    List<SystemCommandOutput> systemCommandOutputs = adminShutdownService.statusShutdown();
    
    assertEquals(1, systemCommandOutputs.size());
    assertEquals(systemCommandOutput, systemCommandOutputs.get(0));
  }

  public List<SystemCommand> mockSetShutdownSystemCommands() {
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("cmd.exe", "/c", "start", "shutdown", "/s", "/t ",
        "5400"));
    systemCommand.setIsDaemon(false);
    systemCommands.add(systemCommand);
    return systemCommands;
  }
  
  public List<SystemCommand> mockCancelShutdownSystemCommands() {
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("cmd.exe", "/c", "start", "shutdown", "/s", "/t ",
        "5400"));
    systemCommand.setIsDaemon(false);
    systemCommands.add(systemCommand);
    return systemCommands;
  }
  
  public List<SystemCommand> mockStatusShutdownSystemCommands() {
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("tasklist", "/FI", "IMAGENAME eq shutdown.exe"));
    systemCommand.setIsDaemon(false);
    systemCommands.add(systemCommand);
    return systemCommands;
  }
  
  private SystemCommandOutput mockSetShutdownCommandOutput() {
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, shutdown, /s, /t , 5400]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(new ArrayList<String>());
    commandOutput.setStandardError(new ArrayList<String>()); 
    return commandOutput;
  }

  private SystemCommandOutput mockCancelShutdownCommandOutput() {
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, shutdown, /a]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(new ArrayList<String>());
    commandOutput.setStandardError(new ArrayList<String>());
    return commandOutput;
  }

  private SystemCommandOutput mockStatusShutdownCommandOutput() { 
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[tasklist, /FI, IMAGENAME eq shutdown.exe]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(Arrays.asList(
        "INFO: No tasks are running which match the specified criteria."));
    commandOutput.setStandardError(new ArrayList<String>()); 
    return commandOutput;
  }
}
