package com.nicobrest.kamehouse.media.video.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.testutils.VideoPlaylistTestUtils;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the VideoPlaylistService class.
 *
 * @author nbrest
 */
public class VideoPlaylistServiceTest {

  private static VideoPlaylistService videoPlaylistService;
  private VideoPlaylistTestUtils videoPlaylistTestUtils = new VideoPlaylistTestUtils();
  private Playlist expectedPlaylist;

  private MockedStatic<PropertiesUtils> propertiesUtils;

  @BeforeAll
  public static void beforeClass() {
    videoPlaylistService = new VideoPlaylistService();
  }

  /**
   * Tests setup.
   */
  @BeforeEach
  public void before() {
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
    when(PropertiesUtils.isWindowsHost()).thenCallRealMethod();
    when(PropertiesUtils.getHostname()).thenReturn(VideoPlaylistTestUtils.MEDIA_SERVER);
    when(PropertiesUtils.getUserHome()).thenReturn(""); // Use git project root as home
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_PLAYLISTS_PATH_LINUX))
        .thenReturn(VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_PLAYLISTS_PATH_WINDOWS))
        .thenReturn(VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_PLAYLISTS_PATH_REMOTE_LAN_SHARE))
        .thenReturn(VideoPlaylistTestUtils.TEST_PLAYLISTS_REMOTE_LAN_SHARE_DIR);
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_PLAYLISTS_PATH_REMOTE_HTTP))
        .thenReturn(VideoPlaylistTestUtils.TEST_PLAYLISTS_REMOTE_HTTP_DIR);
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_MEDIA_SERVER_NAME))
        .thenCallRealMethod();
    videoPlaylistTestUtils.initTestData();
    expectedPlaylist = videoPlaylistTestUtils.getSingleTestData();
  }

  @AfterEach
  public void close() {
    propertiesUtils.close();
  }

  /** Gets all video playlists successful test. */
  @Test
  public void getAllLocalMediaServerTest() {
    videoPlaylistTestUtils.clearFiles();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll();

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /** Gets all video playlists from remote media server successful test. */
  @Test
  public void getAllRemoteMediaServerTest() {
    when(PropertiesUtils.getHostname()).thenReturn("niko-kh-client");

    videoPlaylistTestUtils.clearFiles();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll();

    assertEquals(expectedPlaylists.size(), returnedPlaylists.size());
    if (PropertiesUtils.isWindowsHost()) {
      assertTrue(
          returnedPlaylists
              .get(0)
              .getPath()
              .contains("lan-share-" + VideoPlaylistTestUtils.MEDIA_SERVER));
    } else {
      assertTrue(
          returnedPlaylists
              .get(0)
              .getPath()
              .contains("http-" + VideoPlaylistTestUtils.MEDIA_SERVER));
    }
  }

  /** Gets all video playlists successful fetching playlist content test. */
  @Test
  public void getAllWithContentTest() {
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll(true);

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /** Get a single video playlist successful test. */
  @Test
  public void getPlaylistTest() {
    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(expectedPlaylist.getPath(), true);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /** Get a single video playlist without fetching content successful test. */
  @Test
  public void getPlaylistWithoutContentTest() {
    videoPlaylistTestUtils.clearFiles();

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(expectedPlaylist.getPath(), false);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /** Get a single video playlist invalid path test. */
  @Test
  public void getPlaylistInvalidPathTest() {
    String invalidPath = expectedPlaylist.getPath() + File.separator + "invalidFile.m3u";

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidPath, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /** Get a single video playlist non supported extension test. */
  @Test
  public void getPlaylistNonSupportedExtensionTest() {
    String invalidExtension = expectedPlaylist.getPath().replace(".m3u", ".pdf");

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidExtension, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /** Get a single video playlist path with non supported .. jumps test. */
  @Test
  public void getPlaylistNonSupportedPathJumpsTest() {
    String invalidPath =
        expectedPlaylist
            .getPath()
            .replace("dc.m3u", ".." + File.separator + "dc" + File.separator + "dc.m3u");

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidPath, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }
}
