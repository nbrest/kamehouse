package com.nicobrest.kamehouse.vlcrc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

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

  @Mock
  VlcPlayer vlcPlayerMock;

  @Mock
  HttpClient httpClientMock;

  @Mock
  HttpResponse httpResponseMock;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcPlayerMock);
    Mockito.reset(httpClientMock);
    Mockito.reset(httpResponseMock);
  }

  /**
   * Execute a command in the VLC Player and return it's status.
   */
  @Test
  public void executeTest() {
    VlcPlayer vlcPlayerSpy = PowerMockito.spy(createTestVlcPlayer());
    InputStream vlcRcStatusInputStream = getInputStreamFromResource("vlc-rc-status.json");
    VlcRcCommand vlcRcCommand = new VlcRcCommand();
    vlcRcCommand.setName("fullscreen");
    try {
      PowerMockito.doReturn(httpResponseMock).when(vlcPlayerSpy, "executeGetRequest", any(),
          any());
      PowerMockito.doReturn(vlcRcStatusInputStream).when(vlcPlayerSpy,
          "getInputStreamFromResponse", any());
      PowerMockito.doReturn(httpClientMock).when(vlcPlayerSpy, "createHttpClient", any());
      PowerMockito.doReturn(200).when(vlcPlayerSpy, "getResponseStatusCode", any());
      VlcRcStatus vlcRcStatus = vlcPlayerSpy.execute(vlcRcCommand);
      // Validate returned VlcRcStatus
      assertEquals("16:9", vlcRcStatus.getAspectRatio());
      assertTrue(vlcRcStatus.getFullscreen());
      assertEquals("42581", vlcRcStatus.getStats().get("displayedPictures").toString());
      List<Map<String, Object>> categoryMapList = vlcRcStatus.getInformation().getCategory();
      Map<String, Object> meta = null;
      for (Map<String, Object> categoryMap : categoryMapList) {
        if (categoryMap.get("name").equals("meta")) {
          meta = categoryMap;
        }
      }
      assertEquals("\"Superman.Unbound.2013.480p.WEB-DL.H264.AC3-XaW.mkv\"", meta.get("filename")
          .toString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Get the status information of the VLC Player.
   */
  @Test
  public void getVlcRcStatusTest() {
    VlcPlayer vlcPlayerSpy = PowerMockito.spy(createTestVlcPlayer());
    InputStream vlcRcStatusInputStream = getInputStreamFromResource("vlc-rc-status.json");
    try {
      PowerMockito.doReturn(httpResponseMock).when(vlcPlayerSpy, "executeGetRequest", any(),
          any());
      PowerMockito.doReturn(vlcRcStatusInputStream).when(vlcPlayerSpy,
          "getInputStreamFromResponse", any());
      PowerMockito.doReturn(httpClientMock).when(vlcPlayerSpy, "createHttpClient", any());
      PowerMockito.doReturn(200).when(vlcPlayerSpy, "getResponseStatusCode", any());
      VlcRcStatus vlcRcStatus = vlcPlayerSpy.getVlcRcStatus();
      // Validate returned VlcRcStatus
      assertEquals("16:9", vlcRcStatus.getAspectRatio());
      assertTrue(vlcRcStatus.getFullscreen());
      assertEquals("42581", vlcRcStatus.getStats().get("displayedPictures").toString());
      List<Map<String, Object>> categoryMapList = vlcRcStatus.getInformation().getCategory();
      Map<String, Object> meta = null;
      for (Map<String, Object> categoryMap : categoryMapList) {
        if (categoryMap.get("name").equals("meta")) {
          meta = categoryMap;
        }
      }
      assertEquals("\"Superman.Unbound.2013.480p.WEB-DL.H264.AC3-XaW.mkv\"", meta.get("filename")
          .toString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Get the current playlist of the VLC Player.
   */
  @Test
  public void getVlcRcPlaylistTest() {
    VlcPlayer vlcPlayerSpy = PowerMockito.spy(createTestVlcPlayer());
    InputStream vlcRcPlaylistInputStream = getInputStreamFromResource("vlc-rc-playlist.json");
    try {
      PowerMockito.doReturn(httpResponseMock).when(vlcPlayerSpy, "executeGetRequest", any(),
          any());
      PowerMockito.doReturn(vlcRcPlaylistInputStream).when(vlcPlayerSpy,
          "getInputStreamFromResponse", any());
      PowerMockito.doReturn(httpClientMock).when(vlcPlayerSpy, "createHttpClient", any());
      PowerMockito.doReturn(200).when(vlcPlayerSpy, "getResponseStatusCode", any());
      List<Map<String, Object>> returnedPlaylist = vlcPlayerSpy.getPlaylist();
      assertEquals(3, returnedPlaylist.size());
      assertEquals("Lleyton Hewitt- Brash teenager to Aussie great.mp4", returnedPlaylist.get(0)
          .get("name"));
      assertEquals("Lleyton Hewitt Special.mp4",returnedPlaylist.get(1).get("name"));
      assertEquals("Lleyton Last On Court Interview.mp4",returnedPlaylist.get(2).get("name"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }


  /**
   * Browse files in the VLC Player server.
   */
  @Test
  public void browseTest() {
    VlcPlayer vlcPlayerSpy = PowerMockito.spy(createTestVlcPlayer());
    InputStream vlcRcFilelistInputStream = getInputStreamFromResource("vlc-rc-filelist.json");
    try {
      PowerMockito.doReturn(httpResponseMock).when(vlcPlayerSpy, "executeGetRequest", any(),
          any());
      PowerMockito.doReturn(vlcRcFilelistInputStream).when(vlcPlayerSpy,
          "getInputStreamFromResponse", any());
      PowerMockito.doReturn(httpClientMock).when(vlcPlayerSpy, "createHttpClient", any());
      PowerMockito.doReturn(200).when(vlcPlayerSpy, "getResponseStatusCode", any());
      List<Map<String, Object>> returnedFilelist = vlcPlayerSpy.browse(null);
      assertEquals(2, returnedFilelist.size());
      assertEquals("C:/", returnedFilelist.get(0).get("name"));
      assertEquals("file:///C:/",returnedFilelist.get(0).get("uri"));
      assertEquals(315543600,returnedFilelist.get(0).get("accessTime"));
      assertEquals("D:/", returnedFilelist.get(1).get("name"));
      assertEquals("file:///D:/",returnedFilelist.get(1).get("uri"));
      assertEquals(315543600,returnedFilelist.get(1).get("accessTime"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
  
  private InputStream getInputStreamFromResource(String resourceName) {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream vlcRcStatusInputStream = classLoader.getResourceAsStream(resourceName);
    return vlcRcStatusInputStream;
  } 
  
  private VlcPlayer createTestVlcPlayer() {
    VlcPlayer vlcPlayer = new VlcPlayer();
    vlcPlayer.setHostname("niko-nba");
    vlcPlayer.setPort(8080);
    vlcPlayer.setUsername("");
    vlcPlayer.setPassword("1");
    return vlcPlayer;
  }
}
