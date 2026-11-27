package com.nicobrest.kamehouse.ztvrc.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcFileListItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data and common test methods to test ZtvRcFileLists.
 *
 * @author nbrest
 */
public class ZtvRcFileListTestUtils extends AbstractTestUtils<List<ZtvRcFileListItem>, Object>
    implements TestUtils<List<ZtvRcFileListItem>, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
  }

  @Override
  public void assertEqualsAllAttributes(
      List<ZtvRcFileListItem> expected, List<ZtvRcFileListItem> returned) {
    assertEquals(expected, returned);
    if (expected != null && returned != null) {
      assertEquals(expected.size(), returned.size());
      for (int i = 0; i < expected.size(); i++) {
        ZtvRcFileListItem expectedItem = expected.get(i);
        ZtvRcFileListItem returnedItem = returned.get(i);
        assertEquals(expectedItem.getType(), returnedItem.getType());
        assertEquals(expectedItem.getName(), returnedItem.getName());
        assertEquals(expectedItem.getPath(), returnedItem.getPath());
        assertEquals(expectedItem.getUri(), returnedItem.getUri());
        assertEquals(expectedItem.getSize(), returnedItem.getSize());
        assertEquals(expectedItem.getAccessTime(), returnedItem.getAccessTime());
        assertEquals(expectedItem.getCreationTime(), returnedItem.getCreationTime());
        assertEquals(expectedItem.getModificationTime(), returnedItem.getModificationTime());
        assertEquals(expectedItem.getUid(), returnedItem.getUid());
        assertEquals(expectedItem.getGid(), returnedItem.getGid());
        assertEquals(expectedItem.getMode(), returnedItem.getMode());
      }
    }
  }

  private void initSingleTestData() {
    // Mapped to the contents of test/resources/ztvrc/ztv-rc-filelist.json
    singleTestData = new ArrayList<>();
    ZtvRcFileListItem fileListItem1 = new ZtvRcFileListItem();
    fileListItem1.setType("dir");
    fileListItem1.setPath("C:/");
    fileListItem1.setName("C:/");
    fileListItem1.setUri("file:///C:/");
    fileListItem1.setAccessTime(315543600);
    fileListItem1.setUid(0);
    fileListItem1.setCreationTime(315543600);
    fileListItem1.setGid(0);
    fileListItem1.setModificationTime(315543600);
    fileListItem1.setMode(16895);
    fileListItem1.setSize(0);
    singleTestData.add(fileListItem1);
    ZtvRcFileListItem fileListItem2 = new ZtvRcFileListItem();
    fileListItem2.setType("dir");
    fileListItem2.setPath("D:/");
    fileListItem2.setName("D:/");
    fileListItem2.setUri("file:///D:/");
    fileListItem2.setAccessTime(315543600);
    fileListItem2.setUid(0);
    fileListItem2.setCreationTime(315543600);
    fileListItem2.setGid(0);
    fileListItem2.setModificationTime(315543600);
    fileListItem2.setMode(16895);
    fileListItem2.setSize(0);
    singleTestData.add(fileListItem2);
  }
}
