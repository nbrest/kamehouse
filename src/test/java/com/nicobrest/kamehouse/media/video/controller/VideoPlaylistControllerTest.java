package com.nicobrest.kamehouse.media.video.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.service.VideoPlaylistService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class VideoPlaylistControllerTest {

  private MockMvc mockMvc;

  @InjectMocks
  private VideoPlaylistController videoPlaylistController;

  @Mock
  private VideoPlaylistService videoPlaylistService;

  private List<Playlist> videoPlaylistsListMock;

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(videoPlaylistService);
    mockMvc = MockMvcBuilders.standaloneSetup(videoPlaylistController).build();

    videoPlaylistsListMock = new ArrayList<Playlist>();
    for (int i = 0; i < 4; i++) {
      Playlist playlist = new Playlist();
      playlist.setCategory("heroes\\marvel\\" + i);
      String playlistName = "marvel_movies_" + i + ".m3u";
      playlist.setName(playlistName);
      playlist.setPath("C:\\Users\\nbrest\\playlists\\" + playlistName);
      videoPlaylistsListMock.add(playlist);
    }
  }

  /**
   * Tests getting all video playlists.
   */
  @Test
  public void getAllVideoPlaylistsTest() {
    when(videoPlaylistService.getAllVideoPlaylists()).thenReturn(videoPlaylistsListMock);

    try {
      mockMvc.perform(get("/api/v1/media/video/playlists")).andDo(print()).andExpect(status()
          .isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(
              jsonPath("$", hasSize(4))).andExpect(jsonPath("$[0].name", equalTo(
                  videoPlaylistsListMock.get(0).getName()))).andExpect(jsonPath("$[0].category",
                      equalTo(videoPlaylistsListMock.get(0).getCategory()))).andExpect(jsonPath(
                          "$[0].path", equalTo(videoPlaylistsListMock.get(0).getPath())));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
    verify(videoPlaylistService, times(1)).getAllVideoPlaylists();
    verifyNoMoreInteractions(videoPlaylistService);
  }
}
