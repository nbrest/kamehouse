package com.nicobrest.kamehouse.commons.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseSystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data and common test methods to test SystemCommandOutputs in all layers of the application.
 *
 * @author nbrest
 */
public class SystemCommandOutputTestUtils {

  private SystemCommand.Output singleTestData = null;
  private List<SystemCommand.Output> testDataList = null;

  public SystemCommand.Output getSingleTestData() {
    return singleTestData;
  }

  public void setSingleTestData(SystemCommand.Output singleTestData) {
    this.singleTestData = singleTestData;
  }

  public List<SystemCommand.Output> getTestDataList() {
    return testDataList;
  }

  public void setTestDataList(List<SystemCommand.Output> testDataList) {
    this.testDataList = testDataList;
  }

  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  /**
   * Checks that the commands in the SystemCommandOutput list match the executed
   * KameHouseSystemCommand.
   */
  public void assertCommandExecutedMatch(
      KameHouseSystemCommand kameHouseSystemCommand,
      List<SystemCommand.Output> returnedSystemCommandOutputs) {
    assertCommandExecutedMatch(
        kameHouseSystemCommand.getSystemCommands(), returnedSystemCommandOutputs);
  }

  /**
   * Checks that the commands in the SystemCommandOutput list match the executed SystemCommands.
   */
  public void assertCommandExecutedMatch(
      List<SystemCommand> systemCommands, List<SystemCommand.Output> returnedSystemCommandOutputs) {
    assertEquals(systemCommands.size(), returnedSystemCommandOutputs.size());
    for (int i = 0; i < systemCommands.size(); i++) {
      assertCommandExecutedMatch(systemCommands.get(i), returnedSystemCommandOutputs.get(i));
    }
  }

  /**
   * Checks that the command in the SystemCommandOutput matches the executed command.
   */
  public void assertCommandExecutedMatch(
      SystemCommand systemCommand, SystemCommand.Output returnedSystemCommandOutput) {
    String expectedCommand = systemCommand.getOutput().getCommand();
    String returnedCommand = returnedSystemCommandOutput.getCommand();
    assertEquals(expectedCommand, returnedCommand);
  }

  /**
   * Checks the SystemCommandOutput fields, except the command string.
   */
  public void assertSystemCommandOutputFields(
      int expectedExitCode,
      int expectedPid,
      String expectedStatus,
      List<String> expectedStandardOutput,
      List<String> expectedStandardError,
      SystemCommand.Output returnedSystemCommandOutput) {
    assertEquals(expectedExitCode, returnedSystemCommandOutput.getExitCode());
    assertEquals(expectedPid, returnedSystemCommandOutput.getPid());
    assertEquals(expectedStatus, returnedSystemCommandOutput.getStatus());
    assertThat(returnedSystemCommandOutput.getStandardOutput(), is(expectedStandardOutput));
    assertThat(returnedSystemCommandOutput.getStandardError(), is(expectedStandardError));
  }

  private void initSingleTestData() {
    singleTestData = new SystemCommand.Output();
    singleTestData.setCommand("[tasklist, /FI, IMAGENAME eq shutdown.exe]");
    singleTestData.setExitCode(0);
    singleTestData.setPid(-1);
    singleTestData.setStatus("completed");
    singleTestData.setStandardOutput(
        Arrays.asList("INFO: No tasks are running which match the specified criteria."));
    singleTestData.setStandardError(new ArrayList<String>());
  }

  private void initTestDataList() {
    SystemCommand.Output systemCommandOutput2 = new SystemCommand.Output();
    systemCommandOutput2.setCommand(
        "[cmd.exe, /c, start, /min, vlc, D:\\Series\\game_of_thrones\\GameOfThrones.m3u]");
    systemCommandOutput2.setExitCode(-1);
    systemCommandOutput2.setPid(-1);
    systemCommandOutput2.setStatus("running");
    systemCommandOutput2.setStandardOutput(null);
    systemCommandOutput2.setStandardError(null);

    SystemCommand.Output systemCommandOutput3 = new SystemCommand.Output();
    systemCommandOutput3.setCommand("[vncdo (hidden from logs as it contains passwords)]");
    systemCommandOutput3.setExitCode(0);
    systemCommandOutput3.setPid(-1);
    systemCommandOutput3.setStatus("completed");
    systemCommandOutput3.setStandardOutput(null);
    systemCommandOutput3.setStandardError(null);

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(systemCommandOutput2);
    testDataList.add(systemCommandOutput3);
  }
}
