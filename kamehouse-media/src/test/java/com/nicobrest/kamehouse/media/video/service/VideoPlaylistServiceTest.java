package com.nicobrest.kamehouse.media.video.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand.Output;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.SshClientUtils;
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
  private final VideoPlaylistTestUtils videoPlaylistTestUtils = new VideoPlaylistTestUtils();
  private Playlist expectedPlaylist;

  private MockedStatic<DockerUtils> dockerUtils;
  private MockedStatic<PropertiesUtils> propertiesUtils;
  private MockedStatic<SshClientUtils> sshClientUtils;

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
    sshClientUtils = Mockito.mockStatic(SshClientUtils.class);
    dockerUtils = Mockito.mockStatic(DockerUtils.class);

    when(PropertiesUtils.isWindowsHost()).thenCallRealMethod();
    when(PropertiesUtils.getHostname()).thenReturn(VideoPlaylistTestUtils.MEDIA_SERVER);
    when(PropertiesUtils.getUserHome()).thenReturn(""); // Use git project root as home
    when(DockerUtils.getUserHome()).thenReturn(""); // Use git project root as home
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_PLAYLISTS_PATH_LINUX))
        .thenReturn(VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_PLAYLISTS_PATH_WINDOWS))
        .thenReturn(VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);
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
    sshClientUtils.close();
    dockerUtils.close();
  }

  /**
   * Gets all video playlists successful test.
   */
  @Test
  public void getAllLocalMediaServerTest() {
    videoPlaylistTestUtils.clearFiles();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll();

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all video playlists from remote media server successful test.
   */
  @Test
  public void getAllRemoteMediaServerTest() {
    when(PropertiesUtils.getHostname()).thenReturn("niko-kh-client");
    when(DockerUtils.getHostname()).thenReturn("niko-kh-client");
    when(DockerUtils.isWindowsHostOrWindowsDockerHost()).thenCallRealMethod();

    videoPlaylistTestUtils.clearFiles();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll();

    assertEquals(expectedPlaylists.size(), returnedPlaylists.size());
    assertTrue(
        returnedPlaylists.get(0).getPath().contains("http-media-server-ip"));
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
   * Gets all video playlists successful fetching playlist content test on docker host.
   */
  @Test
  public void getAllWithContentOnWindowsDockerHostTest() {
    videoPlaylistTestUtils.setWindowsPaths();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();
    when(DockerUtils.getDockerHostIp()).thenReturn("1.2.3.4");
    when(DockerUtils.getDockerHostUsername()).thenReturn("gohan");
    when(DockerUtils.isDockerControlHostEnabled()).thenReturn(true);
    when(DockerUtils.isDockerContainer()).thenReturn(true);
    when(DockerUtils.isWindowsDockerHost()).thenReturn(true);
    when(DockerUtils.shouldControlDockerHost()).thenReturn(true);
    when(DockerUtils.isWindowsHostOrWindowsDockerHost()).thenReturn(true);
    Output playlistFilePaths = new Output();
    playlistFilePaths.setStandardOutput(List.of("--------" + expectedPlaylists.get(0).getPath()
        + "\r\n" + expectedPlaylists.get(1).getPath() + "\r\n"));
    Output dcPlaylistContent = new Output();
    dcPlaylistContent.setStandardOutput(
        List.of("#EXTM3UN:\\movies\\heroes\\dc\\Batman - 1\\Batman 1989"
            + ".mp4\r\nN:\\movies\\heroes\\dc\\Batman - 2 - Returns\\Batman Returns 1992.mp4"));
    Output marvelPlaylistContent = new Output();
    marvelPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3UN:\\movies\\heroes\\marvel\\Avengers Infinity War\\Avengers.Infinity.War"
                + ".mp4\r\nN:\\movies\\heroes\\marvel\\Avengers.Age.of.Ultron.2015\\Avengers.Age"
                + ".of.Ultron.2015.mkv\r\nN:\\movies\\heroes\\marvel\\Avengers.The.2012\\The."
                + "Avengers.2012.mkv\r\n"));
    when(SshClientUtils.executeShell(any(), any(), any(), anyBoolean())).thenReturn(
        playlistFilePaths, dcPlaylistContent, marvelPlaylistContent);

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll(true);

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all video playlists successful fetching playlist content test on docker host.
   */
  @Test
  public void getAllWithContentOnLinuxDockerHostTest() {
    videoPlaylistTestUtils.setLinuxPaths();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();
    when(DockerUtils.getDockerHostIp()).thenReturn("1.2.3.4");
    when(DockerUtils.getDockerHostUsername()).thenReturn("gohan");
    when(DockerUtils.isDockerControlHostEnabled()).thenReturn(true);
    when(DockerUtils.isDockerContainer()).thenReturn(true);
    when(DockerUtils.isWindowsDockerHost()).thenReturn(false);
    when(DockerUtils.shouldControlDockerHost()).thenReturn(true);
    when(DockerUtils.isWindowsHostOrWindowsDockerHost()).thenReturn(false);
    Output playlistFilePaths = new Output();
    playlistFilePaths.setStandardOutput(List.of(expectedPlaylists.get(0).getPath()
        + "\n" + expectedPlaylists.get(1).getPath() + "\n"));
    Output dcPlaylistContent = new Output();
    dcPlaylistContent.setStandardOutput(
        List.of("#EXTM3UN:\\movies\\heroes\\dc\\Batman - 1\\Batman 1989"
            + ".mp4\nN:\\movies\\heroes\\dc\\Batman - 2 - Returns\\Batman Returns 1992.mp4"));
    Output marvelPlaylistContent = new Output();
    marvelPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3UN:\\movies\\heroes\\marvel\\Avengers Infinity War\\Avengers.Infinity.War"
                + ".mp4\nN:\\movies\\heroes\\marvel\\Avengers.Age.of.Ultron.2015\\Avengers.Age"
                + ".of.Ultron.2015.mkv\nN:\\movies\\heroes\\marvel\\Avengers.The.2012\\The."
                + "Avengers.2012.mkv\n"));
    when(SshClientUtils.executeShell(any(), any(), any(), anyBoolean())).thenReturn(
        playlistFilePaths, dcPlaylistContent, marvelPlaylistContent);

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll(true);

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Get a single video playlist successful test.
   */
  @Test
  public void getPlaylistTest() {
    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(expectedPlaylist.getPath(), true);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single video playlist without fetching content successful test.
   */
  @Test
  public void getPlaylistWithoutContentTest() {
    videoPlaylistTestUtils.clearFiles();

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(expectedPlaylist.getPath(), false);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single video playlist invalid path test.
   */
  @Test
  public void getPlaylistInvalidPathTest() {
    String invalidPath = expectedPlaylist.getPath() + File.separator + "invalidFile.m3u";

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidPath, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /**
   * Get a single video playlist non supported extension test.
   */
  @Test
  public void getPlaylistNonSupportedExtensionTest() {
    String invalidExtension = expectedPlaylist.getPath().replace(".m3u", ".pdf");

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidExtension, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /**
   * Get a single video playlist path with non supported .. jumps test.
   */
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
