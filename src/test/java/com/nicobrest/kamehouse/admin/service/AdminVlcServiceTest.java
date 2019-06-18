package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.admin.model.AdminVlcCommand;
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

/**
 * Unit tests for the AdminVlcService class.
 * 
 * @author nbrest
 *
 */
public class AdminVlcServiceTest {

  @InjectMocks
  private AdminVlcService adminVlcService;

  @Mock
  private SystemCommandService systemCommandService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(systemCommandService);
  }

  /**
   * Start VLC player successful test.
   */
  @Test
  public void startVlcPlayerTest() {
    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand(AdminVlcCommand.START);
    adminVlcCommand.setFile("/home/goku/playlists/marvel.m3u");
    List<SystemCommand> systemCommands = mockStartVlcPlayerSystemCommands();
    when(systemCommandService.getSystemCommands(adminVlcCommand)).thenReturn(systemCommands);
    SystemCommandOutput systemCommandOutput = mockStartVlcPlayerCommandOutput();
    when(systemCommandService.execute(any())).thenReturn(systemCommandOutput);

    List<SystemCommandOutput> systemCommandOutputs = adminVlcService.startVlcPlayer(
        adminVlcCommand);

    assertEquals(1, systemCommandOutputs.size());
    assertEquals(systemCommandOutput, systemCommandOutputs.get(0));
  }

  /**
   * Start VLC player invalid command test.
   */
  @Test
  public void startVlcPlayerInvalidCommandTest() {
    thrown.expect(KameHouseInvalidCommandException.class);
    thrown.expectMessage("Invalid AdminVlcCommand ");

    AdminVlcCommand adminVlcCommand = new AdminVlcCommand();
    adminVlcCommand.setCommand("InvalidCommand"); 

    adminVlcService.startVlcPlayer(adminVlcCommand);
  }

  /**
   * Stop VLC player successful test.
   */
  @Test
  public void stopVlcPlayerTest() {
    List<SystemCommand> systemCommands = mockStopVlcPlayerSystemCommands();
    when(systemCommandService.getSystemCommands(any(AdminVlcCommand.class))).thenReturn(
        systemCommands);
    SystemCommandOutput systemCommandOutput = mockStopVlcPlayerCommandOutput();
    when(systemCommandService.execute(any())).thenReturn(systemCommandOutput);

    List<SystemCommandOutput> systemCommandOutputs = adminVlcService.stopVlcPlayer();

    assertEquals(1, systemCommandOutputs.size());
    assertEquals(systemCommandOutput, systemCommandOutputs.get(0));
  }

  /**
   * Status VLC successful test.
   */
  @Test
  public void statusVlcPlayerTest() {
    List<SystemCommand> systemCommands = mockStatusVlcPlayerSystemCommands();
    when(systemCommandService.getSystemCommands(any(AdminVlcCommand.class))).thenReturn(
        systemCommands);
    SystemCommandOutput systemCommandOutput = mockStatusVlcPlayerCommandOutput();
    when(systemCommandService.execute(any())).thenReturn(systemCommandOutput);

    List<SystemCommandOutput> systemCommandOutputs = adminVlcService.statusVlcPlayer();

    assertEquals(1, systemCommandOutputs.size());
    assertEquals(systemCommandOutput, systemCommandOutputs.get(0));
  }

  /**
   * Mock start VLC commands.
   */
  private List<SystemCommand> mockStartVlcPlayerSystemCommands() {
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("cmd.exe", "/c", "start", "vlc"));
    systemCommand.setIsDaemon(false);
    systemCommands.add(systemCommand);
    return systemCommands;
  }

  /**
   * Mock stop VLC commands.
   */
  private List<SystemCommand> mockStopVlcPlayerSystemCommands() {
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("skill", "-9", "vlc"));
    systemCommand.setIsDaemon(false);
    systemCommands.add(systemCommand);
    return systemCommands;
  }

  /**
   * Mock status VLC commands.
   */
  private List<SystemCommand> mockStatusVlcPlayerSystemCommands() {
    List<SystemCommand> systemCommands = new ArrayList<SystemCommand>();
    SystemCommand systemCommand = new SystemCommand();
    systemCommand.setCommand(Arrays.asList("tasklist", "/FI", "IMAGENAME eq vlc.exe"));
    systemCommand.setIsDaemon(false);
    systemCommands.add(systemCommand);
    return systemCommands;
  }

  /**
   * Mock start VLC command output.
   */
  private SystemCommandOutput mockStartVlcPlayerCommandOutput() {
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, vlc, /home/goku/playlists/marvel.m3u]");
    commandOutput.setExitCode(-1);
    commandOutput.setPid(-1);
    commandOutput.setStatus("running");
    commandOutput.setStandardOutput(null);
    commandOutput.setStandardError(null);
    return commandOutput;
  }

  /**
   * Mock stop VLC command output.
   */
  private SystemCommandOutput mockStopVlcPlayerCommandOutput() {
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[cmd.exe, /c, start, taskkill, /im, vlc.exe]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(new ArrayList<String>());
    commandOutput.setStandardError(new ArrayList<String>());
    return commandOutput;
  }

  /**
   * Mock status VLC command output.
   */
  private SystemCommandOutput mockStatusVlcPlayerCommandOutput() {
    SystemCommandOutput commandOutput = new SystemCommandOutput();
    commandOutput.setCommand("[tasklist, /FI, IMAGENAME eq vlc.exe]");
    commandOutput.setExitCode(0);
    commandOutput.setPid(-1);
    commandOutput.setStatus("completed");
    commandOutput.setStandardOutput(Arrays.asList(
        "INFO: No tasks are running which match the specified criteria."));
    commandOutput.setStandardError(new ArrayList<String>());
    return commandOutput;
  }
}
