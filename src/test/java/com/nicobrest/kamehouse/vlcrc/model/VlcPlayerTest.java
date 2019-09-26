package com.nicobrest.kamehouse.vlcrc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;

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
  private VlcPlayer vlcPlayer;

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

    VlcRcStatus vlcRcStatus = vlcPlayer.execute(vlcRcCommand);

    assertEquals("16:9", vlcRcStatus.getAspectRatio());
    assertTrue(vlcRcStatus.getFullscreen());
    assertEquals("1988", vlcRcStatus.getStats().get("displayedPictures").toString());
    List<Map<String, Object>> categoryMapList = vlcRcStatus.getInformation().getCategory();
    Map<String, Object> meta = null;
    for (Map<String, Object> categoryMap : categoryMapList) {
      if (categoryMap.get("name").equals("meta")) {
        meta = categoryMap;
      }
    }
    assertEquals("\"1 - Winter Is Coming.avi\"", meta.get("filename").toString());
    // TODO add a VlcRcStatus object in VlcRcStatusTestUtils with the values
    // from the file vlcrc/vlc-rc-status.json and assert all attributes with the
    // test util.
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
    assertEquals("0", vlcRcStatus.getStats().get("displayedPictures").toString());
    List<Map<String, Object>> categoryMapList = vlcRcStatus.getInformation().getCategory();
    Map<String, Object> meta = null;
    for (Map<String, Object> categoryMap : categoryMapList) {
      if (categoryMap.get("name").equals("meta")) {
        meta = categoryMap;
      }
    }
    assertEquals("\"1 - Winter Is Coming.avi\"", meta.get("filename").toString());
    // TODO add a VlcRcStatus object in VlcRcStatusTestUtils with the values
    // from the file loaded and assert all attributes with the test util.
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

    VlcRcStatus vlcRcStatus = vlcPlayer.getVlcRcStatus();

    assertEquals("16:9", vlcRcStatus.getAspectRatio());
    assertTrue(vlcRcStatus.getFullscreen());
    assertEquals("1988", vlcRcStatus.getStats().get("displayedPictures").toString());
    List<Map<String, Object>> categoryMapList = vlcRcStatus.getInformation().getCategory();
    Map<String, Object> meta = null;
    for (Map<String, Object> categoryMap : categoryMapList) {
      if (categoryMap.get("name").equals("meta")) {
        meta = categoryMap;
      }
    }
    assertEquals("\"1 - Winter Is Coming.avi\"", meta.get("filename").toString());
    // TODO add a VlcRcStatus object in VlcRcStatusTestUtils with the values
    // from the file loaded and assert all attributes with the test util.
  }

  /**
   * Get the current playlist of the VLC Player.
   */
  @Test
  public void getVlcRcPlaylistTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-playlist.json");

    List<Map<String, Object>> returnedPlaylist = vlcPlayer.getPlaylist();

    assertEquals(3, returnedPlaylist.size());
    assertEquals("Lleyton Hewitt- Brash teenager to Aussie great.mp4", returnedPlaylist.get(0).get(
        "name"));
    assertEquals("Lleyton Hewitt Special.mp4", returnedPlaylist.get(1).get("name"));
    assertEquals("Lleyton Last On Court Interview.mp4", returnedPlaylist.get(2).get("name"));
    // TODO add a Playlist object in VlcRcPlaylistTestUtils with the values
    // from the file loaded and assert all attributes with the test util.
  }

  /**
   * Browse files in the VLC Player server.
   */
  @Test
  public void browseTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-filelist.json");

    List<Map<String, Object>> returnedFilelist = vlcPlayer.browse("C:/");

    assertEquals(2, returnedFilelist.size());
    // TODO add a filelist object in VlcRcFileListTestUtils with the values
    // from the file loaded and assert all attributes with the test util.
  }

  /**
   * Browse files in the VLC Player server.
   */
  @Test
  public void browseEmptyParameterTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-filelist.json");

    List<Map<String, Object>> returnedFilelist = vlcPlayer.browse(null);

    assertEquals(2, returnedFilelist.size());
    assertEquals("C:/", returnedFilelist.get(0).get("name"));
    assertEquals("file:///C:/", returnedFilelist.get(0).get("uri"));
    assertEquals(315543600, returnedFilelist.get(0).get("accessTime"));
    assertEquals("D:/", returnedFilelist.get(1).get("name"));
    assertEquals("file:///D:/", returnedFilelist.get(1).get("uri"));
    assertEquals(315543600, returnedFilelist.get(1).get("accessTime"));
    // TODO add a filelist object in VlcRcFileListTestUtils with the values
    // from the file loaded and assert all attributes with the test util.
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
