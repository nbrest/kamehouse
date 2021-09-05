package com.nicobrest.kamehouse.vlcrc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractControllerTest;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcFileListTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcPlaylistTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcStatusTestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for the VlcRcController.
 *
 * @author nbrest
 */
public class VlcRcControllerTest extends AbstractControllerTest {

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

  /**
   * Tests setup.
   */
  @BeforeEach
  public void beforeTest() {
    vlcRcStatusTestUtils.initTestData();
    vlcRcStatus = vlcRcStatusTestUtils.getSingleTestData();
    vlcRcPlaylistTestUtils.initTestData();
    vlcRcPlaylist = vlcRcPlaylistTestUtils.getSingleTestData();
    vlcRcFileListTestUtils.initTestData();
    vlcRcFileList = vlcRcFileListTestUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(vlcRcServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(vlcRcController).build();
  }

  /**
   * Tests getting the status information of the VLC Player passed through the URL.
   */
  @Test
  public void getVlcRcStatusTest() throws Exception {
    Mockito.reset(vlcRcServiceMock);
    when(vlcRcServiceMock.getVlcRcStatus("niko-nba")).thenReturn(vlcRcStatus);

    MockHttpServletResponse response = doGet(
        VlcPlayerTestUtils.API_V1_VLCPLAYERS + "niko-nba/status");
    VlcRcStatus responseBody = getResponseBody(response, VlcRcStatus.class);

    verifyResponseStatus(response, HttpStatus.OK);
    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, responseBody);
    verify(vlcRcServiceMock, times(1)).getVlcRcStatus(anyString());
  }

  /**
   * Tests getting 404 not found when the server can't reach the specified vlc player.
   */
  @Test
  public void getVlcRcStatusNotFoundTest() throws Exception {
    Mockito.reset(vlcRcServiceMock);
    when(vlcRcServiceMock.getVlcRcStatus("niko-nba")).thenReturn(null);

    MockHttpServletResponse response = doGet(
        VlcPlayerTestUtils.API_V1_VLCPLAYERS + "niko-nba/status");

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
    Mockito.reset(vlcRcServiceMock);
    when(vlcRcServiceMock.execute(any(), anyString())).thenReturn(vlcRcStatus);
    byte[] requestPayload = JsonUtils.toJsonByteArray(vlcRcCommand);

    MockHttpServletResponse response =
        doPost(VlcPlayerTestUtils.API_V1_VLCPLAYERS + "niko-nba/commands", requestPayload);
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
    Mockito.reset(vlcRcServiceMock);
    when(vlcRcServiceMock.getPlaylist("niko-nba")).thenReturn(vlcRcPlaylist);

    MockHttpServletResponse response = doGet(
        VlcPlayerTestUtils.API_V1_VLCPLAYERS + "niko-nba/playlist");
    List<VlcRcPlaylistItem> responseBody = getResponseBodyList(response, VlcRcPlaylistItem.class);

    vlcRcPlaylistTestUtils.assertEqualsAllAttributes(vlcRcPlaylist, responseBody);
    verify(vlcRcServiceMock, times(1)).getPlaylist(anyString());
  }

  /**
   * Tests browsing files in the VLC Player.
   */
  @Test
  public void browseTest() throws Exception {
    Mockito.reset(vlcRcServiceMock);
    when(vlcRcServiceMock.browse(null, "niko-nba")).thenReturn(vlcRcFileList);

    MockHttpServletResponse response = doGet(
        VlcPlayerTestUtils.API_V1_VLCPLAYERS + "niko-nba/browse");
    List<VlcRcFileListItem> responseBody = getResponseBodyList(response, VlcRcFileListItem.class);

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, responseBody);
    verify(vlcRcServiceMock, times(1)).browse(any(), anyString());
  }
}
