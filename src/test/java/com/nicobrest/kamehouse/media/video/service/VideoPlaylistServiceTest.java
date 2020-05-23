package com.nicobrest.kamehouse.media.video.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.main.utils.PropertiesUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.testutils.VideoPlaylistTestUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Unit tests for the VideoPlaylistService class.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesUtils.class })
public class VideoPlaylistServiceTest {

  private static VideoPlaylistService videoPlaylistService;
  private VideoPlaylistTestUtils videoPlaylistTestUtils = new VideoPlaylistTestUtils();

  @BeforeClass
  public static void beforeClass() {
    videoPlaylistService = new VideoPlaylistService();
  }

  @Before
  public void before() {
    PowerMockito.mockStatic(PropertiesUtils.class);
    videoPlaylistTestUtils.initTestData();

  }

  /**
   * Gets all video playlists successful test.
   */
  @Test
  public void getAllTest() {
    List<String> expectedPlaylists = VideoPlaylistTestUtils.TEST_PLAYLIST_NAMES;
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(PropertiesUtils.getUserHome()).thenReturn("./");
    when(PropertiesUtils.getMediaVideoProperty(anyString())).thenReturn(
        VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll();

    assertEquals(expectedPlaylists.size(), returnedPlaylists.size());
    for (Playlist returnedPlaylist : returnedPlaylists) {
      assertTrue(expectedPlaylists.contains(returnedPlaylist.getName()));
    } 
  }

  /**
   * Get a single video playlist successful test.
   */
  @Test
  public void getPlaylistTest() {
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(PropertiesUtils.getUserHome()).thenReturn("./");
    when(PropertiesUtils.getMediaVideoProperty(anyString())).thenReturn(
        VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);
    String playlistFilename = VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR + "heroes/dc/dc.m3u";
    Path playlistPath = Paths.get(playlistFilename);
    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(playlistPath, true);
    System.out.println(returnedPlaylist);
  }

  //TODO: ADD MORE UNIT TESTS, USE TESTUTILS TO VALIDATE OUTPUTS
}
