package com.nicobrest.kamehouse.vlcrc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcFileListTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcPlaylistTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcStatusTestUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Test class for the VlcPlayer.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ VlcPlayer.class })
public class VlcPlayerTest {

  private VlcPlayerTestUtils vlcPlayerTestUtils = new VlcPlayerTestUtils();
  private VlcRcStatusTestUtils vlcRcStatusTestUtils = new VlcRcStatusTestUtils();
  private VlcRcPlaylistTestUtils vlcRcPlaylistTestUtils = new VlcRcPlaylistTestUtils();
  private VlcRcFileListTestUtils vlcRcFileListTestUtils = new VlcRcFileListTestUtils();
  private VlcPlayer vlcPlayer;
  private VlcRcStatus vlcRcStatus;
  private List<VlcRcPlaylistItem> vlcRcPlaylist;
  private List<VlcRcFileListItem> vlcRcFileList;

  @Mock
  VlcPlayer vlcPlayerMock;

  @Mock
  HttpClient httpClientMock;

  @Mock
  HttpResponse httpResponseMock;

  @Before
  public void init() throws Exception {
    vlcPlayerTestUtils.initTestData();
    vlcPlayer = PowerMockito.spy(vlcPlayerTestUtils.getSingleTestData());
    vlcRcStatusTestUtils.initTestData();
    vlcRcStatus = vlcRcStatusTestUtils.getSingleTestData();
    vlcRcPlaylistTestUtils.initTestData();
    vlcRcPlaylist = vlcRcPlaylistTestUtils.getSingleTestData();
    vlcRcFileListTestUtils.initTestData();
    vlcRcFileList = vlcRcFileListTestUtils.getSingleTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcPlayerMock);
    Mockito.reset(httpClientMock);
    Mockito.reset(httpResponseMock);

    PowerMockito.doReturn(httpClientMock).when(vlcPlayer, "createHttpClient", any());
    PowerMockito.doReturn(httpResponseMock).when(vlcPlayer, "executeGetRequest", any(), any());
  }

  /**
   * Execute a command in the VLC Player and return it's status.
   */
  @Test
  public void executeTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status.json");
    VlcRcCommand vlcRcCommand = new VlcRcCommand();
    vlcRcCommand.setName("fullscreen");

    VlcRcStatus returnedVlcRcStatus = vlcPlayer.execute(vlcRcCommand);

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedVlcRcStatus);
  }

  /**
   * Execute a command in the VLC Player and return it's status.
   */
  @Test
  public void executeCommandWithAllParametersTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status-equalizer.json");
    VlcRcCommand vlcRcCommand = new VlcRcCommand();
    vlcRcCommand.setName("pl_play");
    vlcRcCommand.setBand("low");
    vlcRcCommand.setId("9");
    vlcRcCommand.setInput("1 - Winter Is Coming.avi");
    vlcRcCommand.setOption("opt-3");
    vlcRcCommand.setVal("val-3");

    VlcRcStatus vlcRcStatus = vlcPlayer.execute(vlcRcCommand);

    assertEquals(null, vlcRcStatus.getAspectRatio());
    assertTrue(!vlcRcStatus.getFullscreen());
    assertEquals(0, vlcRcStatus.getStats().getDisplayedPictures());
    List<Map<String, Object>> categoryMapList = vlcRcStatus.getInformation().getCategory();
    Map<String, Object> meta = null;
    for (Map<String, Object> categoryMap : categoryMapList) {
      if (categoryMap.get("name").equals("meta")) {
        meta = categoryMap;
      }
    }
    assertEquals("1 - Winter Is Coming.avi", meta.get("filename"));
  }

  /**
   * Execute a command in the VLC Player with an invalid requestUrl.
   */
  @Test
  public void executeNullCommandTest() {
    VlcRcCommand vlcRcCommand = new VlcRcCommand();
    vlcRcCommand.setName(null);

    VlcRcStatus vlcRcStatus = vlcPlayer.execute(vlcRcCommand);

    assertEquals(null, vlcRcStatus);
  }

  /**
   * Get the status information of the VLC Player.
   */
  @Test
  public void getVlcRcStatusTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status.json");

    VlcRcStatus returnedVlcRcStatus = vlcPlayer.getVlcRcStatus();

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedVlcRcStatus);
  }

  /**
   * Get the current playlist of the VLC Player.
   */
  @Test
  public void getVlcRcPlaylistTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-playlist.json");

    List<VlcRcPlaylistItem> returnedPlaylist = vlcPlayer.getPlaylist();

    vlcRcPlaylistTestUtils.assertEqualsAllAttributes(vlcRcPlaylist, returnedPlaylist);
  }

  /**
   * Browse files in the VLC Player server.
   */
  @Test
  public void browseTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-filelist.json");

    List<VlcRcFileListItem> returnedFilelist = vlcPlayer.browse("C:/");

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, returnedFilelist);
  }

  /**
   * Browse files in the VLC Player server.
   */
  @Test
  public void browseEmptyParameterTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-filelist.json");

    List<VlcRcFileListItem> returnedFilelist = vlcPlayer.browse(null);

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, returnedFilelist);
  }

  /**
   * Setup input stream mock from files in test resources.
   */
  private void setupInputStreamMock(String resourceName) throws Exception {
    InputStream vlcRcFilelistInputStream = VlcPlayerTestUtils.getInputStreamFromResource(
        resourceName);
    PowerMockito.doReturn(vlcRcFilelistInputStream).when(vlcPlayer, "getInputStreamFromResponse",
        any());
  }
}
