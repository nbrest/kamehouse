package com.nicobrest.kamehouse.media.video.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.utils.PropertiesUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

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

  @Test
  public void getAllVideoPlaylistsTest() {
    List<Playlist> expectedPlaylists = getExpectedPlaylists();
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
    when(PropertiesUtils.getUserHome()).thenReturn("./");
    when(PropertiesUtils.getMediaVideoProperty(anyString())).thenReturn(
        "src/test/resources/media.video/playlists/");

    List<Playlist> returnedPlaylists = videoPlaylistService.getAllVideoPlaylists();

    assertEquals(expectedPlaylists.size(), returnedPlaylists.size()); 
    for (Playlist expectedPlaylist : expectedPlaylists) {
      assertTrue(returnedPlaylists.contains(expectedPlaylist));
    }
  }

  private List<Playlist> getExpectedPlaylists() {
    List<Playlist> expectedPlaylists = new ArrayList<Playlist>();
    Playlist expectedMarvelPlaylist = new Playlist();
    expectedMarvelPlaylist.setName("marvel.m3u");
    expectedMarvelPlaylist.setCategory("heroes\\marvel");
    expectedMarvelPlaylist.setPath(
        ".\\src\\test\\resources\\media.video\\playlists\\heroes\\marvel\\marvel.m3u");
    expectedPlaylists.add(expectedMarvelPlaylist);
    Playlist expectedDcPlaylist = new Playlist();
    expectedDcPlaylist.setName("dc.m3u");
    expectedDcPlaylist.setCategory("heroes\\dc");
    expectedDcPlaylist.setPath(
        ".\\src\\test\\resources\\media.video\\playlists\\heroes\\dc\\dc.m3u");
    expectedPlaylists.add(expectedDcPlaylist);
    return expectedPlaylists;
  }
}
