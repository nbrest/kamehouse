package com.nicobrest.kamehouse.commons.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.CoreMatchers;

/**
 * Test data and common test methods to test KameHouseCommandResults in all layers of the
 * application.
 *
 * @author nbrest
 */
public class KameHouseCommandResultTestUtils extends
    AbstractTestUtils<KameHouseCommandResult, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(
      KameHouseCommandResult expectedEntity, KameHouseCommandResult returnedEntity) {
    assertEquals(expectedEntity, returnedEntity);
    assertEquals(expectedEntity.getCommand(), returnedEntity.getCommand());
    assertEquals(expectedEntity.getExitCode(), returnedEntity.getExitCode());
    assertEquals(expectedEntity.getPid(), returnedEntity.getPid());
    assertEquals(expectedEntity.getStatus(), returnedEntity.getStatus());
    assertThat(
        returnedEntity.getStandardOutput(), CoreMatchers.is(expectedEntity.getStandardOutput()));
    assertThat(
        returnedEntity.getStandardError(), CoreMatchers.is(expectedEntity.getStandardError()));
  }

  /**
   * Checks that the commands in the KameHouseCommandResult list match the executed kamehouse
   * commands.
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
    singleTestData = new KameHouseCommandResult(new KameHouseCommand() {
      @Override
      public KameHouseCommandResult execute() {
        return null;
      }

      @Override
      public void init() {
        // nothing to do
      }

      @Override
      public boolean executeOnDockerHost() {
        return false;
      }

      @Override
      public String getCommand() {
        return "[tasklist, /FI, IMAGENAME eq shutdown.exe]";
      }

      @Override
      public boolean hasSensitiveInformation() {
        return false;
      }
    });
    singleTestData.setExitCode(0);
    singleTestData.setPid(-1);
    singleTestData.setStatus("completed");
    singleTestData.setStandardOutput(
        Arrays.asList("INFO: No tasks are running which match the specified criteria."));
  }

  private void initTestDataList() {
    KameHouseCommandResult kameHouseCommandResult2 = new KameHouseCommandResult(
        new KameHouseCommand() {
          @Override
          public KameHouseCommandResult execute() {
            return null;
          }

          @Override
          public void init() {
            // nothing to do
          }

          @Override
          public boolean executeOnDockerHost() {
            return false;
          }

          @Override
          public String getCommand() {
            return
                "[cmd.exe, /c, start, /min, vlc, D:\\Series\\game_of_thrones\\GameOfThrones.m3u]";
          }

          @Override
          public boolean hasSensitiveInformation() {
            return false;
          }
        });
    kameHouseCommandResult2.setExitCode(-1);
    kameHouseCommandResult2.setPid(-1);
    kameHouseCommandResult2.setStatus("running");

    KameHouseCommandResult kameHouseCommandResult3 = new KameHouseCommandResult(
        new KameHouseCommand() {
          @Override
          public KameHouseCommandResult execute() {
            return null;
          }

          @Override
          public void init() {
            // nothing to do
          }

          @Override
          public boolean executeOnDockerHost() {
            return false;
          }

          @Override
          public String getCommand() {
            return "[vncdo (hidden from logs as it contains passwords)]";
          }

          @Override
          public boolean hasSensitiveInformation() {
            return false;
          }
        });
    kameHouseCommandResult3.setExitCode(0);
    kameHouseCommandResult3.setPid(-1);
    kameHouseCommandResult3.setStatus("completed");

    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    testDataList.add(kameHouseCommandResult2);
    testDataList.add(kameHouseCommandResult3);
  }
}
