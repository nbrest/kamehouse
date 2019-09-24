package com.nicobrest.kamehouse.media.video.testutils;

import com.nicobrest.kamehouse.media.video.model.Playlist;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data and common test methods to test VideoPlaylists in all layers of the application.
 * 
 * @author nbrest
 *
 */
public class VideoPlaylistTestUtils {

  public static final String API_V1_MEDIA_VIDEO_PLAYLISTS = "/api/v1/media/video/playlists";
  public static final String TEST_PLAYLISTS_ROOT_DIR = "src/test/resources/media.video/playlists/";
  public static final List<String> TEST_PLAYLIST_NAMES = Arrays.asList("dc.m3u", "marvel.m3u");
  
  private static Playlist singleTestData;
  private static List<Playlist> testDataList; 
  
  static {
    initTestData();
  }
  
  public static Playlist getSingleTestData() {
    return singleTestData;
  }

  public static List<Playlist> getTestDataList() {
    return testDataList;
  }
 
  public static void initTestData() {
    initSingleTestData();
    initTestDataList(); 
  }
  
  public static void initSingleTestData() {
    singleTestData = new Playlist();
    singleTestData.setCategory("heroes\\marvel\\" + 0);
    String playlistName = "marvel_movies_" + 0 + ".m3u";
    singleTestData.setName(playlistName);
    singleTestData.setPath("C:\\Users\\nbrest\\playlists\\" + playlistName);
  }
   
  public static void initTestDataList() { 
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
