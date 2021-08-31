package com.nicobrest.kamehouse.vlcrc.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data and common test methods to test VlcRcFileLists.
 *
 * @author nbrest
 */
public class VlcRcFileListTestUtils extends AbstractTestUtils<List<VlcRcFileListItem>, Object>
    implements TestUtils<List<VlcRcFileListItem>, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
  }

  @Override
  public void assertEqualsAllAttributes(
      List<VlcRcFileListItem> expected, List<VlcRcFileListItem> returned) {
    assertEquals(expected, returned);
    if (expected != null && returned != null) {
      assertEquals(expected.size(), returned.size());
      for (int i = 0; i < expected.size(); i++) {
        VlcRcFileListItem expectedItem = expected.get(i);
        VlcRcFileListItem returnedItem = returned.get(i);
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
    // Mapped to the contents of test/resources/vlcrc/vlc-rc-filelist.json
    singleTestData = new ArrayList<>();
    VlcRcFileListItem fileListItem1 = new VlcRcFileListItem();
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
    VlcRcFileListItem fileListItem2 = new VlcRcFileListItem();
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
