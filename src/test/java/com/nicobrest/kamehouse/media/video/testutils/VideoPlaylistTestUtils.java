package com.nicobrest.kamehouse.media.video.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;

import java.util.ArrayList;
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
public class VideoPlaylistTestUtils extends AbstractTestUtils<Playlist, Object> implements
    TestUtils<Playlist, Object> {

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
    assertEquals(expectedPlaylist, returnedPlaylist);
    assertEquals(expectedPlaylist.getName(), returnedPlaylist.getName());
    assertEquals(expectedPlaylist.getPath(), returnedPlaylist.getPath());
    assertEquals(expectedPlaylist.getCategory(), returnedPlaylist.getCategory());
    assertThat(returnedPlaylist.getFiles(), is(expectedPlaylist.getFiles()));
  }

  private void initSingleTestData() {
    singleTestData = new Playlist();
    singleTestData.setCategory("heroes\\dc");
    String playlistName = "dc.m3u";
    singleTestData.setName(playlistName);
    singleTestData.setPath(TEST_PLAYLISTS_ROOT_DIR + playlistName);
    List<String> files = new ArrayList<>();
    files.add("N:\\movies\\heroes\\dc\\Batman - 1\\Batman 1989.mp4");
    files.add("N:\\movies\\heroes\\dc\\Batman - 2 - Returns\\Batman Returns 1992.mp4");
    singleTestData.setFiles(null);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    Playlist playlist = new Playlist();
    playlist.setCategory("heroes\\marvel");
    String playlistName = "marvel.m3u";
    playlist.setName(playlistName);
    playlist.setPath(TEST_PLAYLISTS_ROOT_DIR + playlistName);
    List<String> files = new ArrayList<>();
    files.add("N:\\movies\\heroes\\marvel\\Avengers Infinity War\\Avengers.Infinity.War.mp4");
    files.add("N:\\movies\\heroes\\marvel\\Avengers.Age.of.Ultron.2015\\Avengers.Age.of.Ultron.2015.mkv");
    files.add("N:\\movies\\heroes\\marvel\\Avengers.The.2012\\The.Avengers.2012.mkv");
    testDataList.add(playlist);
  }
}
