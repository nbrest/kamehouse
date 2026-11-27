package com.nicobrest.kamehouse.ztvrc.testutils;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcCommand;
import java.util.LinkedList;

/**
 * Test data and common test methods for ZtvRcCommands.
 *
 * @author nbrest
 */
public class ZtvRcCommandTestUtils extends AbstractTestUtils<ZtvRcCommand, Object>
    implements TestUtils<ZtvRcCommand, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  private void initSingleTestData() {
    singleTestData = new ZtvRcCommand();
    singleTestData.setName("fullscreen");
  }

  private void initTestDataList() {
    ZtvRcCommand ztvRcCommand2 = new ZtvRcCommand();
    ztvRcCommand2.setName("pl_play");
    ztvRcCommand2.setBand("low");
    ztvRcCommand2.setId("9");
    ztvRcCommand2.setInput("1 - Winter Is Coming.avi");
    ztvRcCommand2.setOption("opt-3");
    ztvRcCommand2.setVal("val-3");

    testDataList = new LinkedList<ZtvRcCommand>();
    testDataList.add(singleTestData);
    testDataList.add(ztvRcCommand2);
  }
}
