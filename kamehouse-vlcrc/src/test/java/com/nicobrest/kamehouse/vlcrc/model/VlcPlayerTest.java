package com.nicobrest.kamehouse.vlcrc.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcPlayerTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcCommandTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcFileListTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcPlaylistTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcStatusTestUtils;
import java.io.InputStream;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test class for the VlcPlayer.
 *
 * @author nbrest
 */
class VlcPlayerTest {

  private VlcPlayerTestUtils vlcPlayerTestUtils = new VlcPlayerTestUtils();
  private VlcRcStatusTestUtils vlcRcStatusTestUtils = new VlcRcStatusTestUtils();
  private VlcRcPlaylistTestUtils vlcRcPlaylistTestUtils = new VlcRcPlaylistTestUtils();
  private VlcRcFileListTestUtils vlcRcFileListTestUtils = new VlcRcFileListTestUtils();
  private VlcRcCommandTestUtils vlcRcCommandTestUtils = new VlcRcCommandTestUtils();
  private VlcPlayer vlcPlayer;
  private VlcRcStatus vlcRcStatus;
  private List<VlcRcPlaylistItem> vlcRcPlaylist;
  private List<VlcRcFileListItem> vlcRcFileList;

  private MockedStatic<HttpClientUtils> httpClientUtilsMock;

  @Mock HttpClient httpClientMock;

  @Mock HttpResponse httpResponseMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  public void init() throws Exception {
    vlcPlayerTestUtils.initTestData();
    vlcPlayer = Mockito.spy(vlcPlayerTestUtils.getSingleTestData());
    vlcRcStatusTestUtils.initTestData();
    vlcRcStatus = vlcRcStatusTestUtils.getSingleTestData();
    vlcRcPlaylistTestUtils.initTestData();
    vlcRcPlaylist = vlcRcPlaylistTestUtils.getSingleTestData();
    vlcRcFileListTestUtils.initTestData();
    vlcRcFileList = vlcRcFileListTestUtils.getSingleTestData();
    vlcRcCommandTestUtils.initTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(httpClientMock);
    Mockito.reset(httpResponseMock);
    httpClientUtilsMock = Mockito.mockStatic(HttpClientUtils.class);
    when(HttpClientUtils.getClient(any(), any())).thenReturn(httpClientMock);
    when(HttpClientUtils.execRequest(any(), any())).thenReturn(httpResponseMock);
    when(HttpClientUtils.urlEncode(any())).thenCallRealMethod();
    when(HttpClientUtils.urlDecode(any())).thenCallRealMethod();
    when(HttpClientUtils.httpGet(any())).thenCallRealMethod();
  }

  @AfterEach
  public void close() {
    httpClientUtilsMock.close();
  }

  /** Executes a command in the VLC Player and return it's status. */
  @Test
  void executeTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status.json");
    VlcRcCommand vlcRcCommand = vlcRcCommandTestUtils.getSingleTestData();

    VlcRcStatus returnedVlcRcStatus = vlcPlayer.execute(vlcRcCommand);

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedVlcRcStatus);
  }

  /** Executes a command in the VLC Player and return it's status. */
  @Test
  void executeCommandWithAllParametersTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status-equalizer.json");
    VlcRcCommand commandWithAllParameters = vlcRcCommandTestUtils.getTestDataList().get(1);

    VlcRcStatus vlcRcStatus = vlcPlayer.execute(commandWithAllParameters);

    assertEquals(null, vlcRcStatus.getAspectRatio());
    assertTrue(!vlcRcStatus.getFullscreen());
    assertEquals(0, vlcRcStatus.getStats().getDisplayedPictures());
    assertEquals("1 - Winter Is Coming.avi", vlcRcStatus.getInformation().getMeta().getFilename());
  }

  /** Executes a command in the VLC Player with an invalid requestUrl. */
  @Test
  void executeNullCommandTest() {
    VlcRcStatus vlcRcStatus = vlcPlayer.execute(new VlcRcCommand());

    assertEquals(null, vlcRcStatus);
  }

  /** Gets the status information of the VLC Player. */
  @Test
  void getVlcRcStatusTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-status.json");

    VlcRcStatus returnedVlcRcStatus = vlcPlayer.getVlcRcStatus();

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedVlcRcStatus);
  }

  /** Gets the current playlist of the VLC Player. */
  @Test
  void getVlcRcPlaylistTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-playlist.json");

    List<VlcRcPlaylistItem> returnedPlaylist = vlcPlayer.getPlaylist();

    vlcRcPlaylistTestUtils.assertEqualsAllAttributes(vlcRcPlaylist, returnedPlaylist);
  }

  /** Browses files in the VLC Player server. */
  @Test
  void browseTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-filelist.json");

    List<VlcRcFileListItem> returnedFilelist = vlcPlayer.browse("C:/");

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, returnedFilelist);
  }

  /** Browses files in the VLC Player server. */
  @Test
  void browseEmptyParameterTest() throws Exception {
    setupInputStreamMock("vlcrc/vlc-rc-filelist.json");

    List<VlcRcFileListItem> returnedFilelist = vlcPlayer.browse(null);

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, returnedFilelist);
  }

  /** Setup input stream mock from files in test resources. */
  private void setupInputStreamMock(String resourceName) throws Exception {
    InputStream inputStream = VlcPlayerTestUtils.getInputStream(resourceName);
    when(HttpClientUtils.getInputStream(any())).thenReturn(inputStream);
  }
}
