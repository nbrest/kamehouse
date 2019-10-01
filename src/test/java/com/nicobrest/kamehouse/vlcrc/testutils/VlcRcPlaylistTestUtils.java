package com.nicobrest.kamehouse.vlcrc.testutils;

import static org.junit.Assert.assertEquals;

import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Test data and common test methods to test DragonBallUsers in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class VlcRcPlaylistTestUtils extends AbstractTestUtils<List<VlcRcPlaylistItem>, Object>
    implements TestUtils<List<VlcRcPlaylistItem>, Object> {

  @Override
  public void initTestData() {
    initSingleTestData();
  }

  @Override
  public void assertEqualsAllAttributes(List<VlcRcPlaylistItem> expected,
      List<VlcRcPlaylistItem> returned) {
    assertEquals(expected, returned);
    if (expected != null && returned != null) {
      assertEquals(expected.size(), returned.size());
      for (int i = 0; i < expected.size(); i++) {
        VlcRcPlaylistItem expectedItem = expected.get(i);
        VlcRcPlaylistItem returnedItem = returned.get(i);
        assertEquals(expectedItem.getId(), returnedItem.getId());
        assertEquals(expectedItem.getName(), returnedItem.getName());
        assertEquals(expectedItem.getUri(), returnedItem.getUri());
        assertEquals(expectedItem.getDuration(), returnedItem.getDuration());
      }
    }
  }

  private void initSingleTestData() {
    // Mapped to the contents of test/resources/vlcrc/vlc-rc-playlist.json
    singleTestData = new ArrayList<>();
    VlcRcPlaylistItem playlistItem1 = new VlcRcPlaylistItem();
    playlistItem1.setId(5);
    playlistItem1.setName("Lleyton Hewitt- Brash teenager to Aussie great.mp4");
    playlistItem1.setUri("file:///home/nbrest/Videos/Lleyton%20"
        + "Hewitt-%20Brash%20teenager%20to%20Aussie%20great.mp4");
    playlistItem1.setDuration(281);
    singleTestData.add(playlistItem1);
    VlcRcPlaylistItem playlistItem2 = new VlcRcPlaylistItem();
    playlistItem2.setId(6);
    playlistItem2.setName("Lleyton Hewitt Special.mp4");
    playlistItem2.setUri("file:///home/nbrest/Videos/Lleyton%20Hewitt%20Special.mp4");
    playlistItem2.setDuration(325);
    singleTestData.add(playlistItem2);
    VlcRcPlaylistItem playlistItem3 = new VlcRcPlaylistItem();
    playlistItem3.setId(7);
    playlistItem3.setName("Lleyton Last On Court Interview.mp4");
    playlistItem3.setUri("file:///home/nbrest/Videos/Lleyton%20Last%20On%20Court%20Interview.mp4");
    playlistItem3.setDuration(426);
    singleTestData.add(playlistItem3);
  }
}
