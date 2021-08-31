package com.nicobrest.kamehouse.vlcrc.testutils;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import java.util.LinkedList;

/**
 * Test data and common test methods for VlcRcCommands.
 *
 * @author nbrest
 */
public class VlcRcCommandTestUtils extends AbstractTestUtils<VlcRcCommand, Object>
    implements TestUtils<VlcRcCommand, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }

  private void initSingleTestData() {
    singleTestData = new VlcRcCommand();
    singleTestData.setName("fullscreen");
  }

  private void initTestDataList() {
    VlcRcCommand vlcRcCommand2 = new VlcRcCommand();
    vlcRcCommand2.setName("pl_play");
    vlcRcCommand2.setBand("low");
    vlcRcCommand2.setId("9");
    vlcRcCommand2.setInput("1 - Winter Is Coming.avi");
    vlcRcCommand2.setOption("opt-3");
    vlcRcCommand2.setVal("val-3");

    testDataList = new LinkedList<VlcRcCommand>();
    testDataList.add(singleTestData);
    testDataList.add(vlcRcCommand2);
  }
}
