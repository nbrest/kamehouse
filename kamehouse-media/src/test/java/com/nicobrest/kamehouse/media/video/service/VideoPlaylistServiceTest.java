package com.nicobrest.kamehouse.media.video.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand.Output;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
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
class VideoPlaylistServiceTest {

  private static VideoPlaylistService videoPlaylistService;
  private final VideoPlaylistTestUtils videoPlaylistTestUtils = new VideoPlaylistTestUtils();
  private Playlist expectedPlaylist;

  private MockedStatic<DockerUtils> dockerUtils;
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
    dockerUtils = Mockito.mockStatic(DockerUtils.class);

    when(PropertiesUtils.isWindowsHost()).thenCallRealMethod();
    when(PropertiesUtils.getHostname()).thenReturn(VideoPlaylistTestUtils.KAMEHOUSE_SERVER);
    when(PropertiesUtils.getUserHome()).thenReturn("."); // Use git project root as home
    when(DockerUtils.getUserHome()).thenReturn("."); // Use git project root as home
    when(PropertiesUtils.getProperty(VideoPlaylistService.PROP_PLAYLISTS_PATH,
        VideoPlaylistService.DEFAULT_PLAYLISTS_PATH))
        .thenReturn(VideoPlaylistTestUtils.TEST_PLAYLISTS_PATH);
    videoPlaylistTestUtils.initTestData();
    expectedPlaylist = videoPlaylistTestUtils.getSingleTestData();
  }

  @AfterEach
  public void close() {
    propertiesUtils.close();
    dockerUtils.close();
  }

  /**
   * Gets all video playlists successful test.
   */
  @Test
  void getAllTest() {
    videoPlaylistTestUtils.clearFiles();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll();

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all video playlists successful fetching playlist content test.
   */
  @Test
  void getAllWithContentTest() {
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll(true);

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all video playlists successful fetching playlist content test on docker host.
   */
  @Test
  void getAllWithContentOnWindowsDockerHostTest() {
    videoPlaylistTestUtils.setWindowsPaths();
    when(DockerUtils.getDockerHostIp()).thenReturn("1.2.3.4");
    when(DockerUtils.getDockerHostUsername()).thenReturn("gohan");
    when(DockerUtils.isDockerControlHostEnabled()).thenReturn(true);
    when(DockerUtils.isDockerContainer()).thenReturn(true);
    when(DockerUtils.isWindowsDockerHost()).thenReturn(true);
    when(DockerUtils.shouldControlDockerHost()).thenReturn(true);
    when(DockerUtils.isWindowsHostOrWindowsDockerHost()).thenReturn(true);
    Output playlistFilePaths = new Output();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();
    playlistFilePaths.setStandardOutput(List.of(expectedPlaylists.get(0).getPath()
        + "\n" + expectedPlaylists.get(1).getPath() + "\n"));
    Output dcPlaylistContent = new Output();
    dcPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U\n"
                + "http://kamehouse-server/streaming/movies/heroes/dc/Batman_1/Batman_1989.mp4\n"
                + "http://kamehouse-server/streaming/movies/heroes/dc/Batman_2_Returns/Batman_Returns_1992.mp4\n"
        )
    );
    Output marvelPlaylistContent = new Output();
    marvelPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U\n"
                + "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers_Infinity_War/Avengers.Infinity.War.mp4\n"
                + "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.Age.of.Ultron.2015/Avengers.Age.of.Ultron.2015.mkv\n"
                + "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.The.2012/The.Avengers.2012.mkv\n"
        )
    );
    when(DockerUtils.executeOnDockerHost(any())).thenReturn(playlistFilePaths, dcPlaylistContent,
        marvelPlaylistContent);

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll(true);

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Gets all video playlists successful fetching playlist content test on docker host.
   */
  @Test
  void getAllWithContentOnLinuxDockerHostTest() {
    videoPlaylistTestUtils.setLinuxPaths();
    when(DockerUtils.getDockerHostIp()).thenReturn("1.2.3.4");
    when(DockerUtils.getDockerHostUsername()).thenReturn("gohan");
    when(DockerUtils.isDockerControlHostEnabled()).thenReturn(true);
    when(DockerUtils.isDockerContainer()).thenReturn(true);
    when(DockerUtils.isWindowsDockerHost()).thenReturn(false);
    when(DockerUtils.shouldControlDockerHost()).thenReturn(true);
    when(DockerUtils.isWindowsHostOrWindowsDockerHost()).thenReturn(false);
    Output playlistFilePaths = new Output();
    List<Playlist> expectedPlaylists = videoPlaylistTestUtils.getTestDataList();
    playlistFilePaths.setStandardOutput(List.of(expectedPlaylists.get(0).getPath()
        + "\n" + expectedPlaylists.get(1).getPath() + "\n"));
    Output dcPlaylistContent = new Output();
    dcPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U\n"
                + "http://kamehouse-server/streaming/movies/heroes/dc/Batman_1/Batman_1989.mp4\n"
                + "http://kamehouse-server/streaming/movies/heroes/dc/Batman_2_Returns/Batman_Returns_1992.mp4\n"
        )
    );
    Output marvelPlaylistContent = new Output();
    marvelPlaylistContent.setStandardOutput(
        List.of(
            "#EXTM3U\n"
                + "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers_Infinity_War/Avengers.Infinity.War.mp4\n"
                + "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.Age.of.Ultron.2015/Avengers.Age.of.Ultron.2015.mkv\n"
                + "http://kamehouse-server/streaming/movies/heroes/marvel/Avengers.The.2012/The.Avengers.2012.mkv\n"
        )
    );
    when(DockerUtils.executeOnDockerHost(any())).thenReturn(playlistFilePaths, dcPlaylistContent,
        marvelPlaylistContent);

    List<Playlist> returnedPlaylists = videoPlaylistService.getAll(true);

    videoPlaylistTestUtils.assertEqualsAllAttributesList(expectedPlaylists, returnedPlaylists);
  }

  /**
   * Get a single video playlist successful test.
   */
  @Test
  void getPlaylistTest() {
    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(expectedPlaylist.getPath(), true);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single video playlist without fetching content successful test.
   */
  @Test
  void getPlaylistWithoutContentTest() {
    videoPlaylistTestUtils.clearFiles();

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(expectedPlaylist.getPath(), false);

    videoPlaylistTestUtils.assertEqualsAllAttributes(expectedPlaylist, returnedPlaylist);
  }

  /**
   * Get a single video playlist invalid path test.
   */
  @Test
  void getPlaylistInvalidPathTest() {
    String invalidPath = expectedPlaylist.getPath() + File.separator + "invalidFile.m3u";

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidPath, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /**
   * Get a single video playlist non supported extension test.
   */
  @Test
  void getPlaylistNonSupportedExtensionTest() {
    String invalidExtension = expectedPlaylist.getPath()
        .replaceAll("movies_dc_all", "movies_dc_invalid")
        .replace(".m3u", ".pdf");

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidExtension, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }

  /**
   * Get a single video playlist path with non supported ".." jumps test.
   */
  @Test
  void getPlaylistNonSupportedPathJumpsTest() {
    String invalidPath =
        expectedPlaylist
            .getPath()
            .replace("movies_dc_all.m3u",
                ".." + File.separator + "movies_dc_all" + File.separator + "movies_dc_all.m3u");

    Playlist returnedPlaylist = videoPlaylistService.getPlaylist(invalidPath, true);

    assertNull(returnedPlaylist, "Expect a null playlist returned");
  }
}
