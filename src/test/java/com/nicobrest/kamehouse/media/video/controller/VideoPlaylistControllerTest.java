package com.nicobrest.kamehouse.media.video.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.main.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.service.VideoPlaylistService;
import com.nicobrest.kamehouse.media.video.testutils.VideoPlaylistTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * Unit tests for VideoPlaylistController class.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class VideoPlaylistControllerTest extends AbstractControllerTest<Playlist, Object> {
  
  private static final String API_V1_MEDIA_VIDEO_PLAYLISTS =
      VideoPlaylistTestUtils.API_V1_MEDIA_VIDEO_PLAYLISTS;
  private List<Playlist> videoPlaylistsList;
  
  @InjectMocks
  private VideoPlaylistController videoPlaylistController;

  @Mock
  private VideoPlaylistService videoPlaylistService;

  @Before
  public void beforeTest() {
    testUtils = new VideoPlaylistTestUtils();
    testUtils.initTestData(); 
    videoPlaylistsList = testUtils.getTestDataList();
    
    MockitoAnnotations.initMocks(this);
    Mockito.reset(videoPlaylistService);
    mockMvc = MockMvcBuilders.standaloneSetup(videoPlaylistController).build();
  }

  /**
   * Tests getting all video playlists.
   */
  @Test
  public void readAllTest() throws Exception {
    when(videoPlaylistService.readAll()).thenReturn(videoPlaylistsList);

    MockHttpServletResponse response = executeGet(API_V1_MEDIA_VIDEO_PLAYLISTS);
    List<Playlist> responseBody = getResponseBodyList(response, Playlist.class);

    verifyResponseStatus(response, HttpStatus.OK);
    verifyContentType(response, MediaType.APPLICATION_JSON_UTF8);
    assertEquals(videoPlaylistsList.size(), responseBody.size());
    assertEquals(videoPlaylistsList, responseBody);
    verify(videoPlaylistService, times(1)).readAll();
    verifyNoMoreInteractions(videoPlaylistService);
  }
}
