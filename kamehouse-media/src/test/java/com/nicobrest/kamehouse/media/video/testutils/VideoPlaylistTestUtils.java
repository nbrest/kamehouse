package com.nicobrest.kamehouse.media.video.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data and common test methods to test VideoPlaylists in all layers of the application.
 *
 * @author nbrest
 */
public class VideoPlaylistTestUtils extends AbstractTestUtils<Playlist, Object>
    implements TestUtils<Playlist, Object> {

  public static final String KAMEHOUSE_SERVER = "kamehouse-server";
  public static final String API_V1_MEDIA_VIDEO_PLAYLIST = "/api/v1/media/video/playlist";
  public static final String API_V1_MEDIA_VIDEO_PLAYLISTS = "/api/v1/media/video/playlists";
  public static final String TEST_PLAYLISTS_PATH =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "media.video"
          + File.separator
          + "playlists"
          + File.separator
          + "video-kamehouse";

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

  /**
   * Unset files.
   */
  public void clearFiles() {
    for (Playlist playlist : testDataList) {
      playlist.setFiles(null);
    }
  }

  public void setLinuxPaths() {
    for (Playlist playlist : testDataList) {
      playlist.setPath(setLinuxPath(playlist.getPath()));
      playlist.setCategory(setLinuxPath(playlist.getCategory()));
    }
  }

  public void setWindowsPaths() {
    for (Playlist playlist : testDataList) {
      playlist.setPath(setWindowsPath(playlist.getPath()));
      playlist.setCategory(setWindowsPath(playlist.getCategory()));
    }
  }

  private String setLinuxPath(String input) {
    return input.replace("\\", "/");
  }

  private String setWindowsPath(String input) {
    return input.replace("/", "\\");
  }

  private void initSingleTestData() {
    singleTestData = getMoviesPlaylist("dc");
    List<String> files = new ArrayList<>();
    files.add("http://kamehouse-server/streaming/movies/heroes/dc/Batman_1/Batman_1989.mp4");
    files.add(
        "http://kamehouse-server/streaming/movies/heroes/dc/Batman_2_Returns/Batman_Returns_1992.mp4");
    singleTestData.setFiles(files);
  }

  private void initTestDataList() {
    testDataList = new LinkedList<>();
    testDataList.add(singleTestData);
    List<String> files = new ArrayList<>();
    files.add(
        "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers_Infinity_War/Avengers.Infinity.War.mp4");
    files.add(
        "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.Age.of.Ultron.2015/Avengers.Age.of.Ultron.2015.mkv");
    files.add(
        "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.The.2012/The.Avengers.2012.mkv");
    Playlist playlist = getMoviesPlaylist("marvel");
    playlist.setFiles(files);
    testDataList.add(playlist);
  }

  private Playlist getMoviesPlaylist(String heroesStudio) {
    Playlist playlist = new Playlist();
    String playlistName = "movies_" + heroesStudio + "_all";
    playlist.setName(playlistName + ".m3u");
    String category = "movies";
    playlist.setPath("." + File.separator + TEST_PLAYLISTS_PATH + File.separator + category
        + File.separator + playlistName + File.separator + playlistName + ".m3u");
    playlist.setCategory(category);
    return playlist;
  }
}
