package com.nicobrest.kamehouse.admin.service;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

import com.nicobrest.kamehouse.admin.model.SystemCommandOutput;
import com.nicobrest.kamehouse.admin.model.admincommand.AdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.VlcStatusAdminCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStartSystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStatusSystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.VncDoKeyPressSystemCommand;
import com.nicobrest.kamehouse.admin.service.SystemCommandService;
import com.nicobrest.kamehouse.main.utils.ProcessUtils;
import com.nicobrest.kamehouse.main.utils.PropertiesUtils;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
  private InputStream processInputStream;
  private InputStream processErrorStream;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void before() {
    PowerMockito.mockStatic(PropertiesUtils.class, ProcessUtils.class);
    systemCommandService = PowerMockito.spy(new SystemCommandService());
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
  }

  /**
   * Execute process successful test.
   */
  @Test
  public void executeAdminCommandTest() throws Exception {
    String inputStreamString = "/home /bin /opt";
    setupProcessStreamMocks(inputStreamString, "");
    AdminCommand adminCommand = new VlcStatusAdminCommand();
    
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(adminCommand);

    assertEquals(adminCommand.getSystemCommands().get(0).getCommand().toString(),
        systemCommandOutputs.get(0).getCommand());
    assertEquals("completed", systemCommandOutputs.get(0).getStatus());
    assertEquals(-1, systemCommandOutputs.get(0).getPid());
    assertEquals(0, systemCommandOutputs.get(0).getExitCode());
    assertEquals(inputStreamString, systemCommandOutputs.get(0).getStandardOutput().get(0));
    assertEquals(new ArrayList<String>(), systemCommandOutputs.get(0).getStandardError());
  }

  /**
   * Execute process successful for linux test.
   */
  @Test
  public void executeLinuxCommandTest() throws Exception {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    String inputStreamString = "/home /bin /opt";
    setupProcessStreamMocks(inputStreamString, "");
    SystemCommand systemCommand = new VlcStatusSystemCommand();
    List<SystemCommand> systemCommands = Arrays.asList(systemCommand); 
    
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);

    assertEquals(systemCommands.size(), systemCommandOutputs.size());
    assertEquals(systemCommand.getCommand().toString(), systemCommandOutputs.get(0).getCommand());
    assertEquals("completed", systemCommandOutputs.get(0).getStatus());
    assertEquals(-1, systemCommandOutputs.get(0).getPid());
    assertEquals(0, systemCommandOutputs.get(0).getExitCode());
    assertEquals(inputStreamString, systemCommandOutputs.get(0).getStandardOutput().get(0));
    assertEquals(new ArrayList<String>(), systemCommandOutputs.get(0).getStandardError());
  }

  /**
   * Execute process with failing VncDo command test.
   */
  @Test
  public void executeVncDoFailedTest() throws Exception {
    String inputStreamString = "/home /bin /opt";
    String errorStreamString = "no errors";
    setupProcessStreamMocks(inputStreamString, errorStreamString);
    when(ProcessUtils.getExitValue(Mockito.any())).thenReturn(1);
    SystemCommand systemCommand = new VncDoKeyPressSystemCommand("esc");
    List<SystemCommand> systemCommands = Arrays.asList(systemCommand);
    
    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);

    assertEquals(systemCommands.size(), systemCommandOutputs.size());
    assertEquals("[vncdo (hidden from logs as it contains passwords)]",
        systemCommandOutputs.get(0).getCommand());
    assertEquals("failed", systemCommandOutputs.get(0).getStatus());
    assertEquals(-1, systemCommandOutputs.get(0).getPid());
    assertEquals(1, systemCommandOutputs.get(0).getExitCode());
    assertEquals(inputStreamString, systemCommandOutputs.get(0).getStandardOutput().get(0));
    assertEquals(errorStreamString, systemCommandOutputs.get(0).getStandardError().get(0));
  }

  /**
   * Execute daemon process successful test.
   */
  @Test
  public void executeDaemonTest() throws Exception {
    setupProcessStreamMocks("","");
    SystemCommand systemCommand = new VlcStartSystemCommand(null);

    SystemCommandOutput systemCommandOutput = systemCommandService.execute(systemCommand);

    assertEquals(systemCommand.getCommand().toString(), systemCommandOutput.getCommand());
    assertEquals("running", systemCommandOutput.getStatus());
    assertEquals(-1, systemCommandOutput.getPid());
    assertEquals(-1, systemCommandOutput.getExitCode());
    assertEquals(null, systemCommandOutput.getStandardOutput());
    assertEquals(null, systemCommandOutput.getStandardError());
  }

  /**
   * Execute process throwing an IOException test.
   */
  @Test
  public void executeIOExceptionTest() throws Exception {
    when(ProcessUtils.getInputStreamFromProcess(Mockito.any())).thenThrow(IOException.class);
    SystemCommand systemCommand = new VlcStatusSystemCommand();
    List<SystemCommand> systemCommands = Arrays.asList(systemCommand);

    List<SystemCommandOutput> systemCommandOutputs = systemCommandService.execute(systemCommands);

    assertEquals(systemCommands.size(), systemCommandOutputs.size());
    assertEquals(systemCommand.getCommand().toString(), systemCommandOutputs.get(0).getCommand());
    assertEquals("failed", systemCommandOutputs.get(0).getStatus());
    assertEquals(-1, systemCommandOutputs.get(0).getPid());
    assertEquals(1, systemCommandOutputs.get(0).getExitCode());
    assertEquals(null, systemCommandOutputs.get(0).getStandardOutput());
    assertEquals("An error occurred executing the command",
        systemCommandOutputs.get(0).getStandardError().get(0));
  }
  
  /**
   * Setup mock input and error streams.
   */
  private void setupProcessStreamMocks(String inputStreamContent, String errorStreamContent) {
    processInputStream = new ByteArrayInputStream(inputStreamContent.getBytes());
    processErrorStream = new ByteArrayInputStream(errorStreamContent.getBytes());
    when(ProcessUtils.getInputStreamFromProcess(Mockito.any())).thenReturn(processInputStream);
    when(ProcessUtils.getErrorStreamFromProcess(Mockito.any())).thenReturn(processErrorStream);
  }
}
