package com.nicobrest.kamehouse.admin.service;

import static org.powermock.api.mockito.PowerMockito.when;

import com.nicobrest.kamehouse.admin.model.SystemCommandOutput;
import com.nicobrest.kamehouse.admin.model.admincommand.AdminCommand;
import com.nicobrest.kamehouse.admin.model.admincommand.ScreenWakeUpAdminCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStartSystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.VlcStatusSystemCommand;
import com.nicobrest.kamehouse.admin.model.systemcommand.VncDoKeyPressSystemCommand;
import com.nicobrest.kamehouse.admin.testutils.SystemCommandOutputTestUtils;
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
  private SystemCommandOutputTestUtils testUtils = new SystemCommandOutputTestUtils();
  private static final List<String> EMPTY_LIST = new ArrayList<>();
  private static final String COMPLETED = "completed";
  private static final String FAILED = "failed";
  private static final String RUNNING = "running";

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
    List<String> inputStream = Arrays.asList("/home /bin /opt");
    setupProcessStreamMocks(inputStream.get(0), "");
    AdminCommand adminCommand = new ScreenWakeUpAdminCommand();

    List<SystemCommandOutput> returnedList = systemCommandService.execute(adminCommand);

    testUtils.assertCommandExecutedMatch(adminCommand, returnedList);
    testUtils.assertSystemCommandOutputFields(0, -1, COMPLETED, inputStream, EMPTY_LIST,
        returnedList.get(0));
    testUtils.assertSystemCommandOutputFields(0, -1, COMPLETED, EMPTY_LIST, EMPTY_LIST,
        returnedList.get(1));
    testUtils.assertSystemCommandOutputFields(0, -1, COMPLETED, EMPTY_LIST, EMPTY_LIST,
        returnedList.get(2));
  }

  /**
   * Execute process successful for linux test.
   */
  @Test
  public void executeLinuxCommandTest() throws Exception {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    List<String> inputStream = Arrays.asList("/home /bin /opt");
    setupProcessStreamMocks(inputStream.get(0), "");
    SystemCommand systemCommand = new VlcStatusSystemCommand();
    List<SystemCommand> systemCommands = Arrays.asList(systemCommand);

    List<SystemCommandOutput> returnedList = systemCommandService.execute(systemCommands);

    testUtils.assertCommandExecutedMatch(systemCommands, returnedList);
    testUtils.assertSystemCommandOutputFields(0, -1, COMPLETED, inputStream, EMPTY_LIST,
        returnedList.get(0));
  }

  /**
   * Execute process with failing VncDo command test.
   */
  @Test
  public void executeVncDoFailedTest() throws Exception {
    List<String> inputStream = Arrays.asList("/home /bin /opt");
    List<String> errorStream = Arrays.asList("no errors");
    setupProcessStreamMocks(inputStream.get(0), errorStream.get(0));
    when(ProcessUtils.getExitValue(Mockito.any())).thenReturn(1);
    SystemCommand systemCommand = new VncDoKeyPressSystemCommand("esc");
    List<SystemCommand> systemCommands = Arrays.asList(systemCommand);

    List<SystemCommandOutput> returnedList = systemCommandService.execute(systemCommands);

    testUtils.assertCommandExecutedMatch(systemCommands, returnedList);
    testUtils.assertSystemCommandOutputFields(1, -1, FAILED, inputStream, errorStream,
        returnedList.get(0));
  }

  /**
   * Execute daemon process successful test.
   */
  @Test
  public void executeDaemonTest() throws Exception {
    setupProcessStreamMocks("", "");
    SystemCommand systemCommand = new VlcStartSystemCommand(null);

    SystemCommandOutput returnedCommandOutput = systemCommandService.execute(systemCommand);

    testUtils.assertCommandExecutedMatch(systemCommand.getCommand(), returnedCommandOutput);
    testUtils.assertSystemCommandOutputFields(-1, -1, RUNNING, null, null, returnedCommandOutput);
  }

  /**
   * Execute process throwing an IOException test.
   */
  @Test
  public void executeIOExceptionTest() throws Exception {
    when(ProcessUtils.getInputStreamFromProcess(Mockito.any())).thenThrow(IOException.class);
    List<String> errorStream = Arrays.asList("An error occurred executing the command");
    SystemCommand systemCommand = new VlcStatusSystemCommand();
    List<SystemCommand> systemCommands = Arrays.asList(systemCommand);

    List<SystemCommandOutput> returnedList = systemCommandService.execute(systemCommands);

    testUtils.assertCommandExecutedMatch(systemCommands, returnedList);
    testUtils.assertSystemCommandOutputFields(1, -1, FAILED, null, errorStream,
        returnedList.get(0));
  }

  /**
   * Setup mock input and error streams.
   */
  private void setupProcessStreamMocks(String inputStreamContent, String errorStreamContent) {
    InputStream processInputStream = new ByteArrayInputStream(inputStreamContent.getBytes());
    InputStream processErrorStream = new ByteArrayInputStream(errorStreamContent.getBytes());
    when(ProcessUtils.getInputStreamFromProcess(Mockito.any())).thenReturn(processInputStream);
    when(ProcessUtils.getErrorStreamFromProcess(Mockito.any())).thenReturn(processErrorStream);
  }
}
