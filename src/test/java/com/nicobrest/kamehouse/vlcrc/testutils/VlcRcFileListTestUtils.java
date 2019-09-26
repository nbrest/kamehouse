package com.nicobrest.kamehouse.vlcrc.testutils;

import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test data and common test methods to test DragonBallUsers in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class VlcRcFileListTestUtils extends AbstractTestUtils<List<Map<String, Object>>, Object>
    implements TestUtils<List<Map<String, Object>>, Object> {
  
  @Override
  public void initTestData() {
    initSingleTestData();
  }

  private void initSingleTestData() { 
    singleTestData = new ArrayList<>();
    Map<String, Object> fileListItem1 = new HashMap<>();
    fileListItem1.put("type", "dir");
    fileListItem1.put("path", "C:\\");
    fileListItem1.put("name", "C:\\");
    fileListItem1.put("uri", "file:///C:/");
    fileListItem1.put("accessTime", 315543600);
    fileListItem1.put("uid", 0);
    fileListItem1.put("creationTime", 315543600);
    fileListItem1.put("gid", 0);
    fileListItem1.put("modificationTime", 315543600);
    fileListItem1.put("mode", 16895);
    fileListItem1.put("size", 0);
    singleTestData.add(fileListItem1);
    Map<String, Object> fileListItem2 = new HashMap<>();
    fileListItem2.put("type", "dir");
    fileListItem2.put("path", "D:\\");
    fileListItem2.put("name", "D:\\");
    fileListItem2.put("uri", "file:///D:/");
    fileListItem2.put("accessTime", 315543600);
    fileListItem2.put("uid", 0);
    fileListItem2.put("creationTime", 315543600);
    fileListItem2.put("gid", 0);
    fileListItem2.put("modificationTime", 315543600);
    fileListItem2.put("mode", 16895);
    fileListItem2.put("size", 0);
    singleTestData.add(fileListItem2);
  }
}
