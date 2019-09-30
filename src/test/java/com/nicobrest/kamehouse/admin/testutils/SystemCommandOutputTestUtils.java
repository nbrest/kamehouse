package com.nicobrest.kamehouse.admin.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.nicobrest.kamehouse.admin.model.SystemCommandOutput;
import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Test data and common test methods to test SystemCommandOutputs in all layers
 * of the application.
 * 
 * @author nbrest
 *
 */
public class SystemCommandOutputTestUtils extends AbstractTestUtils<SystemCommandOutput, Object>
    implements TestUtils<SystemCommandOutput, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  @Override
  public void assertEqualsAllAttributes(SystemCommandOutput expectedEntity,
      SystemCommandOutput returnedEntity) {
    assertEquals(expectedEntity.getCommand(), returnedEntity.getCommand());
    assertEquals(expectedEntity.getExitCode(), returnedEntity.getExitCode());
    assertEquals(expectedEntity.getPid(), returnedEntity.getPid());
    assertEquals(expectedEntity.getStatus(), returnedEntity.getStatus());
    assertThat(expectedEntity.getStandardOutput(), is(returnedEntity.getStandardOutput()));
    assertThat(expectedEntity.getStandardError(), is(returnedEntity.getStandardError()));
  }

  private void initSingleTestData() {
    singleTestData = new SystemCommandOutput();
    singleTestData.setCommand("[tasklist, /FI, IMAGENAME eq shutdown.exe]");
    singleTestData.setExitCode(0);
    singleTestData.setPid(-1);
    singleTestData.setStatus("completed");
    singleTestData.setStandardOutput(Arrays.asList(
        "INFO: No tasks are running which match the specified criteria."));
    singleTestData.setStandardError(new ArrayList<String>());
  }

  private void initTestDataList() {
    SystemCommandOutput systemCommandOutput2 = new SystemCommandOutput();
    systemCommandOutput2.setCommand(
        "[cmd.exe, /c, start, vlc, D:\\Series\\game_of_thrones\\GameOfThrones.m3u]");
    systemCommandOutput2.setExitCode(-1);
    systemCommandOutput2.setPid(-1);
    systemCommandOutput2.setStatus("running");
    systemCommandOutput2.setStandardOutput(null);
    systemCommandOutput2.setStandardError(null);

    SystemCommandOutput systemCommandOutput3 = new SystemCommandOutput();
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
