package com.nicobrest.kamehouse.media.video.controller;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.service.VideoPlaylistService;
import com.nicobrest.kamehouse.media.video.testutils.VideoPlaylistTestUtils;
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
 * Unit tests for VideoPlaylistController class.
 *
 * @author nbrest
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@WebAppConfiguration
class VideoPlaylistControllerTest extends AbstractControllerTest<Playlist, Object> {

  private static final String API_V1_MEDIA_VIDEO_PLAYLISTS =
      VideoPlaylistTestUtils.API_V1_MEDIA_VIDEO_PLAYLISTS;
  private static final String API_V1_MEDIA_VIDEO_PLAYLIST =
      VideoPlaylistTestUtils.API_V1_MEDIA_VIDEO_PLAYLIST;
  private List<Playlist> videoPlaylistsList;

  @InjectMocks
  private VideoPlaylistController videoPlaylistController;

  @Mock
  private VideoPlaylistService videoPlaylistService;

  /**
   * Tests setup.
   */
  @BeforeEach
  void beforeTest() {
    testUtils = new VideoPlaylistTestUtils();
    testUtils.initTestData();
    videoPlaylistsList = testUtils.getTestDataList();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(videoPlaylistService);
    mockMvc = MockMvcBuilders.standaloneSetup(videoPlaylistController).build();
  }

  /**
   * Tests getting all video playlists.
   */
  @Test
  void getAllTest() throws Exception {
    when(videoPlaylistService.getAll()).thenReturn(videoPlaylistsList);

    MockHttpServletResponse response = doGet(API_V1_MEDIA_VIDEO_PLAYLISTS);
    List<Playlist> responseBody = getResponseBodyList(response, Playlist.class);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributesList(videoPlaylistsList, responseBody);
    verify(videoPlaylistService, times(1)).getAll();
    verifyNoMoreInteractions(videoPlaylistService);
  }

  /**
   * Tests getting a specific video playlist.
   */
  @Test
  void getPlaylistTest() throws Exception {
    Playlist expectedPlaylist = testUtils.getSingleTestData();
    when(videoPlaylistService.getPlaylist(anyString(), anyBoolean())).thenReturn(expectedPlaylist);

    MockHttpServletResponse response =
        doGet(API_V1_MEDIA_VIDEO_PLAYLIST + "?path=/home/goku/movies/dc.m3u");
    Playlist responseBody = getResponseBody(response, Playlist.class);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON);
    testUtils.assertEqualsAllAttributes(expectedPlaylist, responseBody);
    verify(videoPlaylistService, times(1)).getPlaylist(anyString(), anyBoolean());
    verifyNoMoreInteractions(videoPlaylistService);
  }
}
