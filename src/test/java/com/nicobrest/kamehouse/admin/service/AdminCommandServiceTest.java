package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.model.AdminCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommand;
import com.nicobrest.kamehouse.systemcommand.model.SystemCommandOutput;
import com.nicobrest.kamehouse.systemcommand.service.SystemCommandService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for AdminCommandService class.
 * 
 * @author nbrest
 *
 */
public class AdminCommandServiceTest {

  @InjectMocks
  private AdminCommandService adminCommandService;

  @Mock
  private SystemCommandService systemCommandService;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(systemCommandService);
  }
  
  /**
   * Execute admin command successful test.
   */
  @Test
  public void executeTest() {
    AdminCommand adminShutdownCommand = new AdminCommand();
    adminShutdownCommand.setCommand(AdminCommand.SHUTDOWN_SET);
    adminShutdownCommand.setTime(5400);
    List<SystemCommand> systemCommands = mockSetShutdownSystemCommands();
    when(systemCommandService.getSystemCommands(adminShutdownCommand)).thenReturn(systemCommands);
    SystemCommandOutput systemCommandOutput = mockSetShutdownCommandOutput();
    List<SystemCommandOutput> mockedSystemCommandOutputs = new ArrayList<SystemCommandOutput>();
    mockedSystemCommandOutputs.add(systemCommandOutput);
    when(systemCommandService.execute(anyList())).thenReturn(mockedSystemCommandOutputs);
    
    List<SystemCommandOutput> systemCommandOutputs = adminCommandService.execute(
        adminShutdownCommand);
    
    assertEquals(1, systemCommandOutputs.size());
    assertEquals(systemCommandOutput, systemCommandOutputs.get(0));
  }

  /**
   * Mock set shutdown commands.
   */
  private List<SystemCommand> mockSetShutdownSystemCommands() {
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("cmd.exe", "/c", "start", "shutdown", "/s", "/t ",
        "5400"));
    systemCommand.setIsDaemon(false);
    systemCommands.add(systemCommand);
    return systemCommands;
  }
  
  /**
   * Mock set shutdown command output.
   */
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
}
