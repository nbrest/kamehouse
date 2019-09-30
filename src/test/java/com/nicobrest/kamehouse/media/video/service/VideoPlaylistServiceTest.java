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

  @BeforeClass
  public static void beforeClass() {
    videoPlaylistService = new VideoPlaylistService();
  }

  @Before
  public void before() {
    PowerMockito.mockStatic(PropertiesUtils.class);
  }

  /**
   * Get all video playlists successful test.
   */
  @Test
  public void readAllTest() {
    List<String> expectedPlaylists = VideoPlaylistTestUtils.TEST_PLAYLIST_NAMES;
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(PropertiesUtils.getUserHome()).thenReturn("./");
    when(PropertiesUtils.getMediaVideoProperty(anyString())).thenReturn(
        VideoPlaylistTestUtils.TEST_PLAYLISTS_ROOT_DIR);

    List<Playlist> returnedPlaylists = videoPlaylistService.readAll();

    assertEquals(expectedPlaylists.size(), returnedPlaylists.size());
    for (Playlist returnedPlaylist : returnedPlaylists) {
      assertTrue(expectedPlaylists.contains(returnedPlaylist.getName()));
    } 
  }
}
