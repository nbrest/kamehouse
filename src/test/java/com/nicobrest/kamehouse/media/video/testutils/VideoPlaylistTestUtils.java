package com.nicobrest.kamehouse.media.video.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;

import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data and common test methods to test VideoPlaylists in all layers of the
 * application.
 * 
 * @author nbrest
 *
 */
public class VideoPlaylistTestUtils extends AbstractTestUtils<Playlist, Object>
    implements TestUtils<Playlist, Object> {

  public static final String API_V1_MEDIA_VIDEO_PLAYLISTS = "/api/v1/media/video/playlists";
  public static final String TEST_PLAYLISTS_ROOT_DIR = "src/test/resources/media.video/playlists/";
  public static final List<String> TEST_PLAYLIST_NAMES = Arrays.asList("dc.m3u", "marvel.m3u");

  @Override
  public void initTestData() {
    initSingleTestData();
    initTestDataList();
  }
  
  @Override
  public void assertEqualsAllAttributes(Playlist expectedPlaylist, Playlist returnedPlaylist) {
    assertEquals(expectedPlaylist.getName(), returnedPlaylist.getName());
    assertEquals(expectedPlaylist.getPath(), returnedPlaylist.getPath());
    assertEquals(expectedPlaylist.getCategory(), returnedPlaylist.getCategory());
    assertEquals(expectedPlaylist.getFiles(), returnedPlaylist.getFiles());
    if (expectedPlaylist.getFiles() != null && returnedPlaylist.getFiles() != null) {
      assertTrue(CollectionUtils.isEqualCollection(expectedPlaylist.getFiles(), returnedPlaylist
          .getFiles()));
    } 
  }

  private void initSingleTestData() {
    singleTestData = new Playlist();
    singleTestData.setCategory("heroes\\marvel\\" + 0);
    String playlistName = "marvel_movies_" + 0 + ".m3u";
    singleTestData.setName(playlistName);
    singleTestData.setPath("C:\\Users\\nbrest\\playlists\\" + playlistName);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<Playlist>();
    testDataList.add(singleTestData);
    for (int i = 1; i < 3; i++) {
      Playlist playlist = new Playlist();
      playlist.setCategory("heroes\\marvel\\" + i);
      String playlistName = "marvel_movies_" + i + ".m3u";
      playlist.setName(playlistName);
      playlist.setPath("C:\\Users\\nbrest\\playlists\\" + playlistName);
      testDataList.add(playlist);
    }
  }
}
