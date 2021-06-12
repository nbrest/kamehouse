package com.nicobrest.kamehouse.vlcrc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcCommandTestUtils;
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

/**
 * Test class for the VlcPlayer.
 * 
 * @author nbrest
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClientUtils.class })
public class VlcPlayerTest {

  private VlcPlayerTestUtils vlcPlayerTestUtils = new VlcPlayerTestUtils();
  private VlcRcStatusTestUtils vlcRcStatusTestUtils = new VlcRcStatusTestUtils();
  private VlcRcPlaylistTestUtils vlcRcPlaylistTestUtils = new VlcRcPlaylistTestUtils();
  private VlcRcFileListTestUtils vlcRcFileListTestUtils = new VlcRcFileListTestUtils();
  private VlcRcCommandTestUtils vlcRcCommandTestUtils = new VlcRcCommandTestUtils();
  private VlcPlayer vlcPlayer;
  private VlcRcStatus vlcRcStatus;
  private List<VlcRcPlaylistItem> vlcRcPlaylist;
  private List<VlcRcFileListItem> vlcRcFileList;

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
    vlcRcCommandTestUtils.initTestData();

    MockitoAnnotations.initMocks(this);
    Mockito.reset(httpClientMock);
    Mockito.reset(httpResponseMock);
    PowerMockito.mockStatic(HttpClientUtils.class);
    when(HttpClientUtils.getClient(any(), any())).thenReturn(httpClientMock);
    when(HttpClientUtils.execRequest(any(), any())).thenReturn(httpResponseMock);
    when(HttpClientUtils.urlEncode(any())).thenCallRealMethod();
    when(HttpClientUtils.urlDecode(any())).thenCallRealMethod();
    when(HttpClientUtils.httpGet(any())).thenCallRealMethod();
  }

  /**
   * Executes a command in the VLC Player and return it's status.
   */
  @Test
  public void executeTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status.json");
    VlcRcCommand vlcRcCommand = vlcRcCommandTestUtils.getSingleTestData();

    VlcRcStatus returnedVlcRcStatus = vlcPlayer.execute(vlcRcCommand);

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedVlcRcStatus);
  }

  /**
   * Executes a command in the VLC Player and return it's status.
   */
  @Test
  public void executeCommandWithAllParametersTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status-equalizer.json");
    VlcRcCommand commandWithAllParameters = vlcRcCommandTestUtils.getTestDataList().get(1);

    VlcRcStatus vlcRcStatus = vlcPlayer.execute(commandWithAllParameters);

    assertEquals(null, vlcRcStatus.getAspectRatio());
    assertTrue(!vlcRcStatus.getFullscreen());
    assertEquals(0, vlcRcStatus.getStats().getDisplayedPictures());
    assertEquals("1 - Winter Is Coming.avi", vlcRcStatus.getInformation().getMeta().getFilename());
  }

  /**
   * Executes a command in the VLC Player with an invalid requestUrl.
   */
  @Test
  public void executeNullCommandTest() {
    VlcRcStatus vlcRcStatus = vlcPlayer.execute(new VlcRcCommand());

    assertEquals(null, vlcRcStatus);
  }

  /**
   * Gets the status information of the VLC Player.
   */
  @Test
  public void getVlcRcStatusTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status.json");

    VlcRcStatus returnedVlcRcStatus = vlcPlayer.getVlcRcStatus();

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedVlcRcStatus);
  }

  /**
   * Gets the current playlist of the VLC Player.
   */
  @Test
  public void getVlcRcPlaylistTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-playlist.json");

    List<VlcRcPlaylistItem> returnedPlaylist = vlcPlayer.getPlaylist();

    vlcRcPlaylistTestUtils.assertEqualsAllAttributes(vlcRcPlaylist, returnedPlaylist);
  }

  /**
   * Browses files in the VLC Player server.
   */
  @Test
  public void browseTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-filelist.json");

    List<VlcRcFileListItem> returnedFilelist = vlcPlayer.browse("C:/");

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, returnedFilelist);
  }

  /**
   * Browses files in the VLC Player server.
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
    InputStream inputStream = VlcPlayerTestUtils.getInputStream(resourceName);
    when(HttpClientUtils.getInputStream(any())).thenReturn(inputStream);
  }
}
