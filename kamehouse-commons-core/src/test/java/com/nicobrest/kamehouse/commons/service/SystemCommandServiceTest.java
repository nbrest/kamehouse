package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.SystemCommandStatus;
import com.nicobrest.kamehouse.commons.model.TestDaemonCommand;
import com.nicobrest.kamehouse.commons.model.TestDaemonKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.TestNonDaemonCommand;
import com.nicobrest.kamehouse.commons.model.TestNonDaemonKameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.testutils.SystemCommandOutputTestUtils;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.codec.Charsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the SystemCommandService class.
 *
 * @author nbrest
 */
class SystemCommandServiceTest {

  private SystemCommandService systemCommandService;
  private SystemCommandOutputTestUtils testUtils = new SystemCommandOutputTestUtils();
  private static final List<String> EMPTY_LIST = new ArrayList<>();
  private static final List<String> INPUT_STREAM_LIST = Arrays.asList("/home /bin /opt");

  private MockedStatic<PropertiesUtils> propertiesUtils;
  private MockedStatic<ProcessUtils> processUtils;
  private MockedStatic<DockerUtils> dockerUtils;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void before() throws InterruptedException {
    testUtils.initTestData();
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
    processUtils = Mockito.mockStatic(ProcessUtils.class);
    dockerUtils = Mockito.mockStatic(DockerUtils.class);
    systemCommandService = Mockito.spy(new SystemCommandService());
    when(ProcessUtils.waitFor(any(), any())).thenReturn(true);
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(DockerUtils.getHostname()).thenReturn("kamehouse-server");
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  public void close() {
    dockerUtils.close();
    propertiesUtils.close();
    processUtils.close();
  }

  /**
   * Executes process successful test.
   */
  @Test
  void execKameHouseSystemCommandTest() {
    setupProcessStreamMocks(INPUT_STREAM_LIST.get(0), "");
    KameHouseSystemCommand kameHouseSystemCommand = new TestNonDaemonKameHouseSystemCommand();

    List<SystemCommand.Output> returnedList = systemCommandService.execute(kameHouseSystemCommand);

    testUtils.assertCommandExecutedMatch(kameHouseSystemCommand, returnedList);
    testUtils.assertSystemCommandOutputFields(
        0, -1, SystemCommandStatus.COMPLETED.getStatus(), INPUT_STREAM_LIST, EMPTY_LIST,
        returnedList.get(0));
  }

  /**
   * Executes process successful for linux test.
   */
  @Test
  void execLinuxCommandTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    setupProcessStreamMocks(INPUT_STREAM_LIST.get(0), "");
    List<SystemCommand> systemCommands = Arrays.asList(new TestNonDaemonCommand());

    List<SystemCommand.Output> returnedList = systemCommandService.execute(systemCommands);

    testUtils.assertCommandExecutedMatch(systemCommands, returnedList);
    testUtils.assertSystemCommandOutputFields(
        0, -1, SystemCommandStatus.COMPLETED.getStatus(), INPUT_STREAM_LIST, EMPTY_LIST,
        returnedList.get(0));
  }

  /**
   * Executes process with failing command test.
   */
  @Test
  void execNonDaemonFailedTest() {
    List<String> errorStream = Arrays.asList("no errors");
    setupProcessStreamMocks(INPUT_STREAM_LIST.get(0), errorStream.get(0));
    when(ProcessUtils.getExitValue(any())).thenReturn(1);
    List<SystemCommand> systemCommands = Arrays.asList(new TestNonDaemonCommand());

    List<SystemCommand.Output> returnedList = systemCommandService.execute(systemCommands);

    testUtils.assertCommandExecutedMatch(systemCommands, returnedList);
    testUtils.assertSystemCommandOutputFields(
        1, -1, SystemCommandStatus.FAILED.getStatus(), INPUT_STREAM_LIST, errorStream,
        returnedList.get(0));
  }

  /**
   * Executes daemon process successful test.
   */
  @Test
  void execDaemonTest() {
    setupProcessStreamMocks("", "");
    SystemCommand systemCommand = new TestDaemonCommand();

    SystemCommand.Output returnedCommandOutput = systemCommandService.execute(systemCommand);

    testUtils.assertCommandExecutedMatch(systemCommand, returnedCommandOutput);
    testUtils.assertSystemCommandOutputFields(-1, -1, SystemCommandStatus.RUNNING.getStatus(), null,
        null, returnedCommandOutput);
  }

  /**
   * Execute command on docker host success test.
   */
  @Test
  void executeOnDockerHostSuccessTest() {
    when(DockerUtils.shouldExecuteOnDockerHost(any())).thenReturn(true);
    when(DockerUtils.executeOnDockerHost(any())).thenReturn(testUtils.getSingleTestData());
    KameHouseSystemCommand kameHouseSystemCommand = new TestDaemonKameHouseSystemCommand();

    List<SystemCommand.Output> returnedList = systemCommandService.execute(kameHouseSystemCommand);

    assertEquals(testUtils.getSingleTestData(), returnedList.get(0));
  }

  /**
   * Execute successful test with sleep set in one of the commands.
   */
  @Test
  void executeWithSleepTimeTest() {
    setupProcessStreamMocks(INPUT_STREAM_LIST.get(0), "");
    KameHouseSystemCommand kameHouseSystemCommand = new TestNonDaemonKameHouseSystemCommand();
    kameHouseSystemCommand.getSystemCommands().get(0).setSleepTime(1);

    List<SystemCommand.Output> returnedList = systemCommandService.execute(kameHouseSystemCommand);

    testUtils.assertCommandExecutedMatch(kameHouseSystemCommand, returnedList);
    testUtils.assertSystemCommandOutputFields(
        0, -1, SystemCommandStatus.COMPLETED.getStatus(), INPUT_STREAM_LIST, EMPTY_LIST,
        returnedList.get(0));
  }

  /**
   * Setup mock input and error streams.
   */
  private void setupProcessStreamMocks(String inputStreamContent, String errorStreamContent) {
    InputStream processInputStream = new ByteArrayInputStream(
        inputStreamContent.getBytes(Charsets.UTF_8));
    InputStream processErrorStream = new ByteArrayInputStream(
        errorStreamContent.getBytes(Charsets.UTF_8));
    when(ProcessUtils.getInputStream(any())).thenReturn(processInputStream);
    when(ProcessUtils.getErrorStream(any())).thenReturn(processErrorStream);
  }
}
