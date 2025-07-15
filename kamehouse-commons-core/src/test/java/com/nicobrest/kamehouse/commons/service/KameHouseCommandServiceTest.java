package com.nicobrest.kamehouse.commons.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.KameHouseCommandStatus;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.TestDaemonCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.TestNonDaemonCommand;
import com.nicobrest.kamehouse.commons.testutils.KameHouseCommandResultCoreTestUtils;
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
 * Unit tests for the KameHouseCommandService class.
 *
 * @author nbrest
 */
class KameHouseCommandServiceTest {

  private KameHouseCommandService kameHouseCommandService;
  private KameHouseCommandResultCoreTestUtils testUtils = new KameHouseCommandResultCoreTestUtils();
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
    kameHouseCommandService = Mockito.spy(new KameHouseCommandService());
    when(ProcessUtils.waitFor(any(), any())).thenReturn(true);
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(DockerUtils.getHostname()).thenReturn("kamehouse-server");
  }

  /**
   * Tests cleanup.
   */
  @AfterEach
  void close() {
    dockerUtils.close();
    propertiesUtils.close();
    processUtils.close();
  }

  /**
   * Executes non daemon process successful test.
   */
  @Test
  void execNonDaemonSuccessTest() {
    setupProcessStreamMocks(INPUT_STREAM_LIST.get(0), "");
    List<KameHouseCommand> kameHouseCommands = List.of(new TestNonDaemonCommand());

    List<KameHouseCommandResult> returnedList = kameHouseCommandService.execute(kameHouseCommands);

    testUtils.assertCommandExecutedMatch(kameHouseCommands, returnedList);
    testUtils.assertKameHouseCommandResultFields(
        0, -1, KameHouseCommandStatus.COMPLETED.getStatus(), INPUT_STREAM_LIST, EMPTY_LIST,
        returnedList.get(0));
  }

  /**
   * Executes non process successful for linux test.
   */
  @Test
  void execLinuxNonDaemonSuccessTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(false);
    setupProcessStreamMocks(INPUT_STREAM_LIST.get(0), "");
    List<KameHouseCommand> kameHouseCommands = Arrays.asList(new TestNonDaemonCommand());

    List<KameHouseCommandResult> returnedList = kameHouseCommandService.execute(kameHouseCommands);

    testUtils.assertCommandExecutedMatch(kameHouseCommands, returnedList);
    testUtils.assertKameHouseCommandResultFields(
        0, -1, KameHouseCommandStatus.COMPLETED.getStatus(), INPUT_STREAM_LIST, EMPTY_LIST,
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
    List<KameHouseCommand> kameHouseCommands = Arrays.asList(new TestNonDaemonCommand());

    List<KameHouseCommandResult> returnedList = kameHouseCommandService.execute(kameHouseCommands);

    testUtils.assertCommandExecutedMatch(kameHouseCommands, returnedList);
    testUtils.assertKameHouseCommandResultFields(
        1, -1, KameHouseCommandStatus.FAILED.getStatus(), INPUT_STREAM_LIST, errorStream,
        returnedList.get(0));
  }

  /**
   * Executes daemon process successful test.
   */
  @Test
  void execDaemonSuccessTest() {
    setupProcessStreamMocks("", "");
    KameHouseCommand kameHouseCommand = new TestDaemonCommand();

    var result = kameHouseCommandService.execute(kameHouseCommand);

    testUtils.assertCommandExecutedMatch(kameHouseCommand, result);
    testUtils.assertKameHouseCommandResultFields(-1, -1, KameHouseCommandStatus.RUNNING.getStatus(),
        new ArrayList<>(),
        new ArrayList<>(), result);
  }

  /**
   * Execute command on docker host success test.
   */
  @Test
  void executeOnDockerHostSuccessTest() {
    when(DockerUtils.shouldExecuteOnDockerHost(any())).thenReturn(true);
    when(DockerUtils.executeOnDockerHost(any())).thenReturn(testUtils.getSingleTestData());
    List<KameHouseCommand> kameHouseCommands = List.of(new TestDaemonCommand());

    List<KameHouseCommandResult> returnedList = kameHouseCommandService.execute(kameHouseCommands);

    assertEquals(testUtils.getSingleTestData(), returnedList.get(0));
  }

  /**
   * Execute successful test with sleep set in one of the commands.
   */
  @Test
  void executeWithSleepTimeSuccessTest() {
    setupProcessStreamMocks(INPUT_STREAM_LIST.get(0), "");
    TestNonDaemonCommand testNonDaemonCommand = new TestNonDaemonCommand();
    testNonDaemonCommand.setSleepTime(1);
    List<KameHouseCommand> kameHouseCommands = List.of(testNonDaemonCommand);

    List<KameHouseCommandResult> returnedList = kameHouseCommandService.execute(kameHouseCommands);

    testUtils.assertCommandExecutedMatch(kameHouseCommands, returnedList);
    testUtils.assertKameHouseCommandResultFields(
        0, -1, KameHouseCommandStatus.COMPLETED.getStatus(), INPUT_STREAM_LIST, EMPTY_LIST,
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
