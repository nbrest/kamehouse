package com.nicobrest.kamehouse.ztvrc.testutils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data and common test methods to test ZtvRcPlaylists.
 *
 * @author nbrest
 */
public class ZtvRcPlaylistTestUtils extends AbstractTestUtils<List<ZtvRcPlaylistItem>, Object>
    implements TestUtils<List<ZtvRcPlaylistItem>, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
  }

  @Override
  public void assertEqualsAllAttributes(
      List<ZtvRcPlaylistItem> expected, List<ZtvRcPlaylistItem> returned) {
    assertEquals(expected, returned);
    if (expected != null && returned != null) {
      assertEquals(expected.size(), returned.size());
      for (int i = 0; i < expected.size(); i++) {
        ZtvRcPlaylistItem expectedItem = expected.get(i);
        ZtvRcPlaylistItem returnedItem = returned.get(i);
        assertEquals(expectedItem.getId(), returnedItem.getId());
        assertEquals(expectedItem.getName(), returnedItem.getName());
        assertEquals(expectedItem.getUri(), returnedItem.getUri());
        assertEquals(expectedItem.getFilename(), returnedItem.getFilename());
        assertEquals(expectedItem.getDuration(), returnedItem.getDuration());
      }
    }
  }

  private void initSingleTestData() {
    // Mapped to the contents of test/resources/ztvrc/ztv-rc-playlist.json
    singleTestData = new ArrayList<>();
    ZtvRcPlaylistItem playlistItem1 = new ZtvRcPlaylistItem();
    playlistItem1.setId(5);
    playlistItem1.setName("Lleyton Hewitt- Brash teenager to Aussie great.mp4");
    playlistItem1.setUri(
        "file:///home/nbrest/Videos/Lleyton%20"
            + "Hewitt-%20Brash%20teenager%20to%20Aussie%20great.mp4");
    playlistItem1.setFilename(
        "/home/nbrest/Videos/Lleyton " + "Hewitt- Brash teenager to Aussie great.mp4");
    playlistItem1.setDuration(281);
    singleTestData.add(playlistItem1);
    ZtvRcPlaylistItem playlistItem2 = new ZtvRcPlaylistItem();
    playlistItem2.setId(6);
    playlistItem2.setName("Lleyton Hewitt Special.mp4");
    playlistItem2.setUri("file:///home/nbrest/Videos/Lleyton%20Hewitt%20Special.mp4");
    playlistItem2.setFilename("/home/nbrest/Videos/Lleyton Hewitt Special.mp4");
    playlistItem2.setDuration(325);
    singleTestData.add(playlistItem2);
    ZtvRcPlaylistItem playlistItem3 = new ZtvRcPlaylistItem();
    playlistItem3.setId(7);
    playlistItem3.setName("Lleyton Last On Court Interview.mp4");
    playlistItem3.setUri("file:///home/nbrest/Videos/Lleyton%20Last%20On%20Court%20Interview.mp4");
    playlistItem3.setFilename("/home/nbrest/Videos/Lleyton Last On Court Interview.mp4");
    playlistItem3.setDuration(426);
    singleTestData.add(playlistItem3);
  }
}
