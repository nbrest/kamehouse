package com.nicobrest.kamehouse.commons.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data and common test methods to test KameHouseCommandResults in all layers of the
 * application.
 *
 * @author nbrest
 */
public class KameHouseCommandResultCoreTestUtils {

  private KameHouseCommandResult singleTestData = null;
  private List<KameHouseCommandResult> testDataList = null;

  public KameHouseCommandResult getSingleTestData() {
    return singleTestData;
  }

  public void setSingleTestData(KameHouseCommandResult singleTestData) {
    this.singleTestData = singleTestData;
  }

  public List<KameHouseCommandResult> getTestDataList() {
    return testDataList;
  }

  public void setTestDataList(List<KameHouseCommandResult> testDataList) {
    this.testDataList = testDataList;
  }

  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  /**
   * Checks that the commands in the KameHouseCommandResult list match the executed
   * KameHouseCommands.
   */
  public void assertCommandExecutedMatch(
      List<KameHouseCommand> kameHouseCommands,
      List<KameHouseCommandResult> returnedKameHouseCommandResults) {
    assertEquals(kameHouseCommands.size(), returnedKameHouseCommandResults.size());
    for (int i = 0; i < kameHouseCommands.size(); i++) {
      assertCommandExecutedMatch(kameHouseCommands.get(i), returnedKameHouseCommandResults.get(i));
    }
  }

  /**
   * Checks that the command in the KameHouseCommandResult matches the executed command.
   */
  public void assertCommandExecutedMatch(
      KameHouseCommand kameHouseCommand, KameHouseCommandResult returnedKameHouseCommandResult) {
    String expectedCommand = kameHouseCommand.getCommand();
    String returnedCommand = returnedKameHouseCommandResult.getCommand();
    assertEquals(expectedCommand, returnedCommand);
  }

  /**
   * Checks the KameHouseCommandResult fields, except the command string.
   */
  public void assertKameHouseCommandResultFields(
      int expectedExitCode,
      int expectedPid,
      String expectedStatus,
      List<String> expectedStandardOutput,
      List<String> expectedStandardError,
      KameHouseCommandResult returnedKameHouseCommandResult) {
    assertEquals(expectedExitCode, returnedKameHouseCommandResult.getExitCode());
    assertEquals(expectedPid, returnedKameHouseCommandResult.getPid());
    assertEquals(expectedStatus, returnedKameHouseCommandResult.getStatus());
    assertThat(returnedKameHouseCommandResult.getStandardOutput(), is(expectedStandardOutput));
    assertThat(returnedKameHouseCommandResult.getStandardError(), is(expectedStandardError));
  }

  private void initSingleTestData() {
    singleTestData = new KameHouseCommandResult();
    singleTestData.setExitCode(0);
    singleTestData.setPid(-1);
    singleTestData.setStatus("completed");
    singleTestData.setStandardOutput(
        Arrays.asList("INFO: No tasks are running which match the specified criteria."));
  }

  private void initTestDataList() {
    KameHouseCommandResult kameHouseCommandResult2 = new KameHouseCommandResult();
    kameHouseCommandResult2.setExitCode(-1);
    kameHouseCommandResult2.setPid(-1);
    kameHouseCommandResult2.setStatus("running");

    KameHouseCommandResult kameHouseCommandResult3 = new KameHouseCommandResult();
    kameHouseCommandResult3.setExitCode(0);
    kameHouseCommandResult3.setPid(-1);
    kameHouseCommandResult3.setStatus("completed");

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(kameHouseCommandResult2);
    testDataList.add(kameHouseCommandResult3);
  }
}
