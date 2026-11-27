package com.nicobrest.kamehouse.ztvrc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandControllerTest;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcCommand;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcFileListItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import com.nicobrest.kamehouse.ztvrc.service.ZtvRcService;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvPlayerTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcFileListTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcPlaylistTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcStatusTestUtils;
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
 * Test class for the ZtvRcController.
 *
 * @author nbrest
 */
class ZtvRcControllerTest extends AbstractKameHouseCommandControllerTest {

  private ZtvRcStatusTestUtils ztvRcStatusTestUtils = new ZtvRcStatusTestUtils();
  private ZtvRcPlaylistTestUtils ztvRcPlaylistTestUtils = new ZtvRcPlaylistTestUtils();
  private ZtvRcFileListTestUtils ztvRcFileListTestUtils = new ZtvRcFileListTestUtils();
  private ZtvRcStatus ztvRcStatus;
  private List<ZtvRcPlaylistItem> ztvRcPlaylist;
  private List<ZtvRcFileListItem> ztvRcFileList;

  @InjectMocks
  private ZtvRcController ztvRcController;

  @Mock(name = "ztvRcService")
  private ZtvRcService ztvRcServiceMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  void beforeTest() {
    ztvRcStatusTestUtils.initTestData();
    ztvRcStatus = ztvRcStatusTestUtils.getSingleTestData();
    ztvRcPlaylistTestUtils.initTestData();
    ztvRcPlaylist = ztvRcPlaylistTestUtils.getSingleTestData();
    ztvRcFileListTestUtils.initTestData();
    ztvRcFileList = ztvRcFileListTestUtils.getSingleTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(ztvRcServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(ztvRcController).build();
  }

  /**
   * Tests getting the status information of the ZTV Player passed through the URL.
   */
  @Test
  void getZtvRcStatusTest() throws Exception {
    Mockito.reset(ztvRcServiceMock);
    when(ztvRcServiceMock.getZtvRcStatus("kamehouse-server")).thenReturn(ztvRcStatus);

    MockHttpServletResponse response = doGet(
        ZtvPlayerTestUtils.API_V1_ZTVPLAYERS + "/kamehouse-server/status");
    ZtvRcStatus responseBody = getResponseBody(response, ZtvRcStatus.class);

    verifyResponseStatus(response, HttpStatus.OK);
    ztvRcStatusTestUtils.assertEqualsAllAttributes(ztvRcStatus, responseBody);
    verify(ztvRcServiceMock, times(1)).getZtvRcStatus(anyString());
  }

  /**
   * Tests getting 404 not found when the server can't reach the specified ZTV Player.
   */
  @Test
  void getZtvRcStatusNotFoundTest() throws Exception {
    Mockito.reset(ztvRcServiceMock);
    when(ztvRcServiceMock.getZtvRcStatus("kamehouse-server")).thenReturn(null);

    MockHttpServletResponse response = doGet(
        ZtvPlayerTestUtils.API_V1_ZTVPLAYERS + "/kamehouse-server/status");

    verifyResponseStatus(response, HttpStatus.NOT_FOUND);
    verify(ztvRcServiceMock, times(1)).getZtvRcStatus(anyString());
  }

  /**
   * Tests Executing a command in the selected ZTV Player.
   */
  @Test
  void execCommandTest() throws Exception {
    ZtvRcCommand ztvRcCommand = new ZtvRcCommand();
    ztvRcCommand.setName("fullscreen");
    Mockito.reset(ztvRcServiceMock);
    when(ztvRcServiceMock.execute(any(), anyString())).thenReturn(ztvRcStatus);
    byte[] requestPayload = JsonUtils.toJsonByteArray(ztvRcCommand);

    MockHttpServletResponse response =
        doPost(ZtvPlayerTestUtils.API_V1_ZTVPLAYERS + "/kamehouse-server/commands", requestPayload);
    ZtvRcStatus responseBody = getResponseBody(response, ZtvRcStatus.class);

    verifyResponseStatus(response, HttpStatus.CREATED);
    ztvRcStatusTestUtils.assertEqualsAllAttributes(ztvRcStatus, responseBody);
    verify(ztvRcServiceMock, times(1)).execute(any(), anyString());
  }

  /**
   * Tests getting the playlist from the ZTV Player.
   */
  @Test
  void getPlaylistTest() throws Exception {
    Mockito.reset(ztvRcServiceMock);
    when(ztvRcServiceMock.getPlaylist("kamehouse-server")).thenReturn(ztvRcPlaylist);

    MockHttpServletResponse response = doGet(
        ZtvPlayerTestUtils.API_V1_ZTVPLAYERS + "/kamehouse-server/playlist");
    List<ZtvRcPlaylistItem> responseBody = getResponseBodyList(response, ZtvRcPlaylistItem.class);

    ztvRcPlaylistTestUtils.assertEqualsAllAttributes(ztvRcPlaylist, responseBody);
    verify(ztvRcServiceMock, times(1)).getPlaylist(anyString());
  }

  /**
   * Tests browsing files in the ZTV Player.
   */
  @Test
  void browseTest() throws Exception {
    Mockito.reset(ztvRcServiceMock);
    when(ztvRcServiceMock.browse(null, "kamehouse-server")).thenReturn(ztvRcFileList);

    MockHttpServletResponse response = doGet(
        ZtvPlayerTestUtils.API_V1_ZTVPLAYERS + "/kamehouse-server/browse");
    List<ZtvRcFileListItem> responseBody = getResponseBodyList(response, ZtvRcFileListItem.class);

    ztvRcFileListTestUtils.assertEqualsAllAttributes(ztvRcFileList, responseBody);
    verify(ztvRcServiceMock, times(1)).browse(any(), anyString());
  }

  /**
   * Tests getting the stats for a running ZTV Player.
   */
  @Test
  void statsTest() throws Exception {
    kameHouseCommandControllerTestSetup();

    String url = ZtvPlayerTestUtils.API_V1_ZTVPLAYERS
        + "/kamehouse-server/stats?fullReport=false&updateStats=false";
    execGetKameHouseCommandsTest(url);
  }

  /**
   * Tests getting the stats for a running ZTV Player.
   */
  @Test
  void statsFullReportTest() throws Exception {
    kameHouseCommandControllerTestSetup();

    String url = ZtvPlayerTestUtils.API_V1_ZTVPLAYERS
        + "/kamehouse-server/stats?fullReport=true&updateStats=false";
    execGetKameHouseCommandsTest(url);
  }
}
