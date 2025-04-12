package com.nicobrest.kamehouse.media.controller;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.media.model.Playlist;
import com.nicobrest.kamehouse.media.service.PlaylistService;
import com.nicobrest.kamehouse.media.testutils.PlaylistTestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for PlaylistController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class PlaylistControllerTest extends AbstractControllerTest<Playlist, Object> {

  private static final String API_V1_MEDIA_PLAYLISTS = PlaylistTestUtils.API_V1_MEDIA_PLAYLISTS;
  private static final String API_V1_MEDIA_PLAYLIST = PlaylistTestUtils.API_V1_MEDIA_PLAYLIST;
  private List<Playlist> playlistsList;

  @InjectMocks
  private PlaylistController playlistController;

  @Mock
  private PlaylistService playlistService;

  /**
   * Tests setup.
   */
  @BeforeEach
  void beforeTest() {
    testUtils = new PlaylistTestUtils();
    testUtils.initTestData();
    playlistsList = testUtils.getTestDataList();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(playlistService);
    mockMvc = MockMvcBuilders.standaloneSetup(playlistController).build();
  }

  /**
   * Tests getting all playlists.
   */
  @Test
  void getAllTest() throws Exception {
    when(playlistService.getAll()).thenReturn(playlistsList);

    MockHttpServletResponse response = doGet(API_V1_MEDIA_PLAYLISTS);
    List<Playlist> responseBody = getResponseBodyList(response, Playlist.class);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributesList(playlistsList, responseBody);
    verify(playlistService, times(1)).getAll();
    verifyNoMoreInteractions(playlistService);
  }

  /**
   * Tests getting a specific playlist.
   */
  @Test
  void getPlaylistTest() throws Exception {
    Playlist expectedPlaylist = testUtils.getSingleTestData();
    when(playlistService.getPlaylist(anyString(), anyBoolean())).thenReturn(expectedPlaylist);

    MockHttpServletResponse response =
        doGet(API_V1_MEDIA_PLAYLIST + "?path=/home/goku/movies/dc-all.m3u");
    Playlist responseBody = getResponseBody(response, Playlist.class);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributes(expectedPlaylist, responseBody);
    verify(playlistService, times(1)).getPlaylist(anyString(), anyBoolean());
    verifyNoMoreInteractions(playlistService);
  }
}
