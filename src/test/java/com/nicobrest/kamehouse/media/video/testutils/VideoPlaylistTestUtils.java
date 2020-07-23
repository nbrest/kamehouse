package com.nicobrest.kamehouse.media.video.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;

import java.io.File;
import java.util.ArrayList;
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

  public static final String API_V1_MEDIA_VIDEO_PLAYLIST = "/api/v1/media/video/playlist";
  public static final String API_V1_MEDIA_VIDEO_PLAYLISTS = "/api/v1/media/video/playlists";
  public static final String TEST_PLAYLISTS_ROOT_DIR = "src" + File.separator + "test"
      + File.separator + "resources" + File.separator + "media.video" + File.separator +
      "playlists" + File.separator + "localhost";
  public static final String TEST_PLAYLISTS_REMOTE_SERVER_DIR = "src" + File.separator + "test"
      + File.separator + "resources" + File.separator + "media.video" + File.separator +
      "playlists" + File.separator + "samba-niko-nba";

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

  public void clearFiles() {
    for(Playlist playlist : testDataList) {
      playlist.setFiles(null);
    }
  }

  private void initSingleTestData() {
    singleTestData = getHeroesPlaylist("dc");
    List<String> files = new ArrayList<>();
    files.add("N:\\movies\\heroes\\dc\\Batman - 1\\Batman 1989.mp4");
    files.add("N:\\movies\\heroes\\dc\\Batman - 2 - Returns\\Batman Returns 1992.mp4");
    singleTestData.setFiles(files);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    Playlist playlist = getHeroesPlaylist("marvel");
    List<String> files = new ArrayList<>();
    files.add("N:\\movies\\heroes\\marvel\\Avengers Infinity War\\Avengers.Infinity.War.mp4");
    files.add("N:\\movies\\heroes\\marvel\\Avengers.Age.of.Ultron.2015\\Avengers.Age.of.Ultron.2015.mkv");
    files.add("N:\\movies\\heroes\\marvel\\Avengers.The.2012\\The.Avengers.2012.mkv");
    playlist.setFiles(files);
    testDataList.add(playlist);
  }

  private Playlist getHeroesPlaylist(String heroesStudio) {
    Playlist playlist = new Playlist();
    String playlistName = heroesStudio + ".m3u";
    playlist.setName(playlistName);
    String category = "heroes" + File.separator + heroesStudio;
    playlist.setPath(TEST_PLAYLISTS_ROOT_DIR + File.separator + category + File.separator
        + playlistName);
    playlist.setCategory(category);
    return playlist;
  }
}
