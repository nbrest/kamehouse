package com.nicobrest.kamehouse.media.video.service;

import static org.junit.Assert.assertNull;
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

import java.io.File;
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
  private Playlist expectedPlaylist;

  @BeforeClass
  public static void beforeClass() {
    videoPlaylistService = new VideoPlaylistService();
  }

  @Before
  public void before() {
    PowerMockito.mockStatic(PropertiesUtils.class);
    when(PropertiesUtils.isWindowsHost()).thenCallRealMethod();
    when(PropertiesUtils.getUserHome()).thenReturn(""); // Use git project root as home
    when(PropertiesUtils.getMediaVideoProperty(anyString())).thenReturn(
        VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);
    videoPlaylistTestUtils.initTestData();
    expectedPlaylist = videoPlaylistTestUtils.getSingleTestData();
  }

  /**
   * Gets all video playlists successful test.
   */
  @Test
  public void getAllTest() {
    videoPlaylistTestUtils.clearFiles();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll();

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all video playlists successful fetching playlist content test.
   */
  @Test
  public void getAllWithContentTest() {
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll(true);

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Get a single video playlist successful test.
   */
  @Test
  public void getPlaylistTest() {
    Path playlistPath = Paths.get(expectedPlaylist.getPath());

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(playlistPath, true);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single video playlist without fetching content successful test.
   */
  @Test
  public void getPlaylistWithoutContentTest() {
    videoPlaylistTestUtils.clearFiles();
    Path playlistPath = Paths.get(expectedPlaylist.getPath());

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(playlistPath, false);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single video playlist invalid path test.
   */
  @Test
  public void getPlaylistInvalidPathTest() {
    String invalidPath = expectedPlaylist.getPath() + File.separator + "invalidFile.m3u";
    Path playlistPath = Paths.get(invalidPath);

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(playlistPath, true);

    assertNull("Expect a null playlist returned", returnedPlaylist);
  }

  /**
   * Get a single video playlist non supported extension test.
   */
  @Test
  public void getPlaylistNonSupportedExtensionTest() {
    String invalidExtension = expectedPlaylist.getPath().replace(".m3u", ".pdf");
    Path playlistPath = Paths.get(invalidExtension);

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(playlistPath, true);

    assertNull("Expect a null playlist returned", returnedPlaylist);
  }

  /**
   * Get a single video playlist path with non supported .. jumps test.
   */
  @Test
  public void getPlaylistNonSupportedPathJumpsTest() {
    String invalidPath = expectedPlaylist.getPath().replace("dc.m3u", ".."
        + File.separator + "dc" + File.separator + "dc.m3u");
    Path playlistPath = Paths.get(invalidPath);

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(playlistPath, true);

    assertNull("Expect a null playlist returned", returnedPlaylist);
  }
}
