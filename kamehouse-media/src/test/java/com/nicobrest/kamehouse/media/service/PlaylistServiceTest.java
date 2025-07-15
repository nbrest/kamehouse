package com.nicobrest.kamehouse.media.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.media.model.Playlist;
import com.nicobrest.kamehouse.media.model.kamehousecommand.GetPlaylistContentKameHouseCommand;
import com.nicobrest.kamehouse.media.testutils.PlaylistTestUtils;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the PlaylistService class.
 *
 * @author nbrest
 */
class PlaylistServiceTest {

  private static PlaylistService playlistService;
  private final PlaylistTestUtils playlistTestUtils = new PlaylistTestUtils();
  private Playlist expectedPlaylist;

  private MockedStatic<DockerUtils> dockerUtils;
  private MockedStatic<PropertiesUtils> propertiesUtils;

  @BeforeAll
  public static void beforeClass() {
    playlistService = new PlaylistService();
  }

  /**
   * Tests setup.
   */
  @BeforeEach
  void before() {
    propertiesUtils = Mockito.mockStatic(PropertiesUtils.class);
    dockerUtils = Mockito.mockStatic(DockerUtils.class);

    when(PropertiesUtils.isWindowsHost()).thenCallRealMethod();
    when(PropertiesUtils.getHostname()).thenReturn(PlaylistTestUtils.KAMEHOUSE_SERVER);
    when(PropertiesUtils.getUserHome()).thenReturn("."); // Use git project root as home
    when(DockerUtils.getUserHome()).thenReturn("."); // Use git project root as home
    when(PropertiesUtils.getProperty(PlaylistService.PROP_PLAYLISTS_PATH,
        PlaylistService.DEFAULT_PLAYLISTS_PATH))
        .thenReturn(PlaylistTestUtils.TEST_PLAYLISTS_PATH);
    when(DockerUtils.getDockerHostPlaylistPath()).thenReturn(
        PlaylistTestUtils.TEST_PLAYLISTS_PATH);
    playlistTestUtils.initTestData();
    expectedPlaylist = playlistTestUtils.getSingleTestData();
  }

  @AfterEach
  void close() {
    propertiesUtils.close();
    dockerUtils.close();
  }

  /**
   * Gets all playlists successful test.
   */
  @Test
  void getAllTest() {
    playlistTestUtils.clearFiles();
    List<Playlist> expectedPlaylists = playlistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = playlistService.getAll();

    playlistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all playlists successful fetching playlist content test.
   */
  @Test
  void getAllWithContentTest() {
    List<Playlist> expectedPlaylists = playlistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = playlistService.getAll(true);

    playlistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all playlists successful fetching playlist content test on docker host.
   */
  @Test
  void getAllWithContentOnWindowsDockerHostTest() {
    playlistTestUtils.setWindowsPaths();
    when(DockerUtils.getDockerHostIp()).thenReturn("1.2.3.4");
    when(DockerUtils.getDockerHostUsername()).thenReturn("gohan");
    when(DockerUtils.isDockerControlHostEnabled()).thenReturn(true);
    when(DockerUtils.isDockerContainer()).thenReturn(true);
    when(DockerUtils.isWindowsDockerHost()).thenReturn(true);
    when(DockerUtils.shouldControlDockerHost()).thenReturn(true);
    when(DockerUtils.isWindowsHostOrWindowsDockerHost()).thenReturn(true);
    KameHouseCommandResult playlistFilePaths = new KameHouseCommandResult(
        new GetPlaylistContentKameHouseCommand("sftp://localhost/test-file.m3u"));
    List<Playlist> expectedPlaylists = playlistTestUtils.getTestDataList();
    playlistFilePaths.setStandardOutput(
        List.of(expectedPlaylists.get(0).getPath(), expectedPlaylists.get(1).getPath()));
    KameHouseCommandResult dcPlaylistContent = new KameHouseCommandResult(
        new GetPlaylistContentKameHouseCommand("sftp://localhost/test-file.m3u"));
    dcPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U",
            "http://kamehouse-server/streaming/movies/heroes/dc/Batman_1/Batman_1989.mp4",
            "http://kamehouse-server/streaming/movies/heroes/dc/Batman_2_Returns/Batman_Returns_1992.mp4"
        )
    );
    KameHouseCommandResult marvelPlaylistContent = new KameHouseCommandResult(
        new GetPlaylistContentKameHouseCommand("sftp://localhost/test-file.m3u"));
    marvelPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U",
            "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers_Infinity_War/Avengers.Infinity.War.mp4",
            "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.Age.of.Ultron.2015/Avengers.Age.of.Ultron.2015.mkv",
            "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.The.2012/The.Avengers.2012.mkv"
        )
    );
    when(DockerUtils.executeOnDockerHost(any())).thenReturn(playlistFilePaths, dcPlaylistContent,
        marvelPlaylistContent);

    List<Playlist> returnedPlaylists = playlistService.getAll(true);

    for (Playlist playlist : expectedPlaylists) {
      // for playlists using docker controlling remote host the playlists path are /
      playlist.setPath(playlist.getPath().replace("\\", "/"));
    }
    playlistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all playlists successful fetching playlist content test on docker host.
   */
  @Test
  void getAllWithContentOnLinuxDockerHostTest() {
    playlistTestUtils.setLinuxPaths();
    when(DockerUtils.getDockerHostIp()).thenReturn("1.2.3.4");
    when(DockerUtils.getDockerHostUsername()).thenReturn("gohan");
    when(DockerUtils.isDockerControlHostEnabled()).thenReturn(true);
    when(DockerUtils.isDockerContainer()).thenReturn(true);
    when(DockerUtils.isWindowsDockerHost()).thenReturn(false);
    when(DockerUtils.shouldControlDockerHost()).thenReturn(true);
    when(DockerUtils.isWindowsHostOrWindowsDockerHost()).thenReturn(false);
    KameHouseCommandResult playlistFilePaths = new KameHouseCommandResult(
        new GetPlaylistContentKameHouseCommand("sftp://localhost/test-file.m3u"));
    List<Playlist> expectedPlaylists = playlistTestUtils.getTestDataList();
    playlistFilePaths.setStandardOutput(
        List.of(expectedPlaylists.get(0).getPath(), expectedPlaylists.get(1).getPath()));
    KameHouseCommandResult dcPlaylistContent = new KameHouseCommandResult(
        new GetPlaylistContentKameHouseCommand("sftp://localhost/test-file.m3u"));
    dcPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U",
            "http://kamehouse-server/streaming/movies/heroes/dc/Batman_1/Batman_1989.mp4",
            "http://kamehouse-server/streaming/movies/heroes/dc/Batman_2_Returns/Batman_Returns_1992.mp4"
        )
    );
    KameHouseCommandResult marvelPlaylistContent = new KameHouseCommandResult(
        new GetPlaylistContentKameHouseCommand("sftp://localhost/test-file.m3u"));
    marvelPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U",
            "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers_Infinity_War/Avengers.Infinity.War.mp4",
            "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.Age.of.Ultron.2015/Avengers.Age.of.Ultron.2015.mkv",
            "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.The.2012/The.Avengers.2012.mkv"
        )
    );
    when(DockerUtils.executeOnDockerHost(any())).thenReturn(playlistFilePaths, dcPlaylistContent,
        marvelPlaylistContent);

    List<Playlist> returnedPlaylists = playlistService.getAll(true);

    playlistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Get a single playlist successful test.
   */
  @Test
  void getPlaylistTest() {
    Playlist returnedPlaylist = playlistService.getPlaylist(expectedPlaylist.getPath(), true);

    playlistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single playlist without fetching content successful test.
   */
  @Test
  void getPlaylistWithoutContentTest() {
    playlistTestUtils.clearFiles();

    Playlist returnedPlaylist = playlistService.getPlaylist(expectedPlaylist.getPath(), false);

    playlistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single playlist invalid path test.
   */
  @Test
  void getPlaylistInvalidPathTest() {
    String invalidPath = expectedPlaylist.getPath() + File.separator + "invalidFile.m3u";

    Playlist returnedPlaylist = playlistService.getPlaylist(invalidPath, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /**
   * Get a single playlist non supported extension test.
   */
  @Test
  void getPlaylistNonSupportedExtensionTest() {
    String invalidExtension = expectedPlaylist.getPath()
        .replaceAll("movies_dc_all", "movies_dc_invalid")
        .replace(".m3u", ".pdf");

    Playlist returnedPlaylist = playlistService.getPlaylist(invalidExtension, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /**
   * Get a single playlist path with not supported ".." jumps test.
   */
  @Test
  void getPlaylistNonSupportedPathJumpsTest() {
    String invalidPath =
        expectedPlaylist
            .getPath()
            .replace("movies_dc_all.m3u",
                ".." + File.separator + "movies_dc_all" + File.separator + "movies_dc_all.m3u");

    Playlist returnedPlaylist = playlistService.getPlaylist(invalidPath, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }
}
