package com.nicobrest.kamehouse.vlcrc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudControllerTest;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.service.VlcPlayerService;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcFileListTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcPlaylistTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcStatusTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

/**
 * Test class for the VlcRcController.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class VlcRcControllerTest extends AbstractCrudControllerTest<VlcPlayer, VlcPlayerDto> {

  private static final String API_V1_VLCPLAYERS = VlcPlayerTestUtils.API_V1_VLCPLAYERS;
  private VlcPlayer vlcPlayer;

  private VlcRcStatusTestUtils vlcRcStatusTestUtils = new VlcRcStatusTestUtils();
  private VlcRcPlaylistTestUtils vlcRcPlaylistTestUtils = new VlcRcPlaylistTestUtils();
  private VlcRcFileListTestUtils vlcRcFileListTestUtils = new VlcRcFileListTestUtils();
  private VlcRcStatus vlcRcStatus;
  private List<VlcRcPlaylistItem> vlcRcPlaylist;
  private List<VlcRcFileListItem> vlcRcFileList;

  @InjectMocks
  private VlcRcController vlcRcController;

  @Mock(name = "vlcRcService")
  private VlcRcService vlcRcServiceMock;

  @Mock(name = "vlcPlayerService")
  private VlcPlayerService vlcPlayerServiceMock;

  @Before
  public void beforeTest() {
    testUtils = new VlcPlayerTestUtils();
    testUtils.initTestData();
    testUtils.setIds();
    vlcPlayer = testUtils.getSingleTestData();

    vlcRcStatusTestUtils.initTestData();
    vlcRcStatus = vlcRcStatusTestUtils.getSingleTestData();
    vlcRcPlaylistTestUtils.initTestData();
    vlcRcPlaylist = vlcRcPlaylistTestUtils.getSingleTestData();
    vlcRcFileListTestUtils.initTestData();
    vlcRcFileList = vlcRcFileListTestUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcRcServiceMock);
    Mockito.reset(vlcPlayerServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(vlcRcController).build();
  }

  /**
   * Tests creating a VLC Player.
   */
  @Test
  public void createTest() throws Exception {
    createTest(API_V1_VLCPLAYERS, vlcPlayerServiceMock);
  }

  /**
   * Tests reading a single vlc player.
   */
  @Test
  public void readTest() throws Exception {
    readTest(API_V1_VLCPLAYERS, vlcPlayerServiceMock, VlcPlayer.class);
  }

  /**
   * Tests getting all VLC Players.
   */
  @Test
  public void readAllTest() throws Exception {
    readAllTest(API_V1_VLCPLAYERS, vlcPlayerServiceMock, VlcPlayer.class);
  }

  /**
   * Tests updating a VLC Player in the system.
   */
  @Test
  public void updateTest() throws Exception {
    updateTest(API_V1_VLCPLAYERS, vlcPlayerServiceMock);
  }

  /**
   * Tests deleting a VLC Player from the system.
   */
  @Test
  public void deleteTest() throws Exception {
    deleteTest(API_V1_VLCPLAYERS, vlcPlayerServiceMock, VlcPlayer.class);
  }

  /**
   * Tests getting a specific VLC Player.
   */
  @Test
  public void getByHostnameTest() throws Exception {
    when(vlcPlayerServiceMock.getByHostname(vlcPlayer.getHostname())).thenReturn(vlcPlayer);

    MockHttpServletResponse response = doGet(API_V1_VLCPLAYERS + "hostname/" + vlcPlayer
        .getHostname());
    VlcPlayer responseBody = getResponseBody(response, VlcPlayer.class);

    verifyResponseStatus(response, HttpStatus.OK);
    testUtils.assertEqualsAllAttributes(vlcPlayer, responseBody);
  }

  /**
   * Tests getting the status information of the VLC Player passed through the
   * URL.
   */
  @Test
  public void getVlcRcStatusTest() throws Exception {
    when(vlcRcServiceMock.getVlcRcStatus("niko-nba")).thenReturn(vlcRcStatus);

    MockHttpServletResponse response = doGet(API_V1_VLCPLAYERS + "niko-nba/status");
    VlcRcStatus responseBody = getResponseBody(response, VlcRcStatus.class);

    verifyResponseStatus(response, HttpStatus.OK);
    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, responseBody);
    verify(vlcRcServiceMock, times(1)).getVlcRcStatus(anyString());
  }

  /**
   * Tests getting 404 not found when the server can't reach the specified vlc
   * player.
   */
  @Test
  public void getVlcRcStatusNotFoundTest() throws Exception {
    when(vlcRcServiceMock.getVlcRcStatus("niko-nba")).thenReturn(null);

    MockHttpServletResponse response = doGet(API_V1_VLCPLAYERS + "niko-nba/status");

    verifyResponseStatus(response, HttpStatus.NOT_FOUND);
    verify(vlcRcServiceMock, times(1)).getVlcRcStatus(anyString());
  }

  /**
   * Tests Executing a command in the selected VLC Player.
   */
  @Test
  public void execCommandTest() throws Exception {
    VlcRcCommand vlcRcCommand = new VlcRcCommand();
    vlcRcCommand.setName("fullscreen");
    when(vlcRcServiceMock.execute(any(), anyString())).thenReturn(vlcRcStatus);
    byte[] requestPayload = JsonUtils.toJsonByteArray(vlcRcCommand);

    MockHttpServletResponse response = doPost(API_V1_VLCPLAYERS + "niko-nba/commands",
        requestPayload);
    VlcRcStatus responseBody = getResponseBody(response, VlcRcStatus.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, responseBody);
    verify(vlcRcServiceMock, times(1)).execute(any(), anyString());
  }

  /**
   * Tests getting the playlist from the VLC Player.
   */
  @Test
  public void getPlaylistTest() throws Exception {
    when(vlcRcServiceMock.getPlaylist("niko-nba")).thenReturn(vlcRcPlaylist);

    MockHttpServletResponse response = doGet(API_V1_VLCPLAYERS + "niko-nba/playlist");
    List<VlcRcPlaylistItem> responseBody = getResponseBodyList(response, VlcRcPlaylistItem.class);

    vlcRcPlaylistTestUtils.assertEqualsAllAttributes(vlcRcPlaylist, responseBody);
    verify(vlcRcServiceMock, times(1)).getPlaylist(anyString());
  }

  /**
   * Tests browsing files in the VLC Player.
   */
  @Test
  public void browseTest() throws Exception {
    when(vlcRcServiceMock.browse(null, "niko-nba")).thenReturn(vlcRcFileList);

    MockHttpServletResponse response = doGet(API_V1_VLCPLAYERS + "niko-nba/browse");
    List<VlcRcFileListItem> responseBody = getResponseBodyList(response, VlcRcFileListItem.class);

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, responseBody);
    verify(vlcRcServiceMock, times(1)).browse(any(), anyString());
  }
}
