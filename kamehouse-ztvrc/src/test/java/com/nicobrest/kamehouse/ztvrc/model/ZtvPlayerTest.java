package com.nicobrest.kamehouse.ztvrc.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.ztvrc.model.VlcPlayer;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvPlayerTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcCommandTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcFileListTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcPlaylistTestUtils;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcStatusTestUtils;
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
 * Test class for the ZTV Player implementation.
 *
 * @author nbrest
 */
class ZtvPlayerTest {

  private ZtvPlayerTestUtils ztvPlayerTestUtils = new ZtvPlayerTestUtils(VlcPlayer::new);
  private ZtvRcStatusTestUtils ztvRcStatusTestUtils = new ZtvRcStatusTestUtils();
  private ZtvRcPlaylistTestUtils ztvRcPlaylistTestUtils = new ZtvRcPlaylistTestUtils();
  private ZtvRcFileListTestUtils ztvRcFileListTestUtils = new ZtvRcFileListTestUtils();
  private ZtvRcCommandTestUtils ztvRcCommandTestUtils = new ZtvRcCommandTestUtils();
  private ZtvPlayer ztvPlayer;
  private ZtvRcStatus expectedZtvRcStatus;
  private List<ZtvRcPlaylistItem> ztvRcPlaylist;
  private List<ZtvRcFileListItem> ztvRcFileList;

  private MockedStatic<HttpClientUtils> httpClientUtilsMock;

  @Mock HttpClient httpClientMock;

  @Mock HttpResponse httpResponseMock;

  /**
   * Tests setup.
   */
  @BeforeEach
  void init() throws Exception {
    ztvPlayerTestUtils.initTestData();
    ztvPlayer = spy(ztvPlayerTestUtils.getSingleTestData());
    ztvRcStatusTestUtils.initTestData();
    expectedZtvRcStatus = ztvRcStatusTestUtils.getSingleTestData();
    ztvRcPlaylistTestUtils.initTestData();
    ztvRcPlaylist = ztvRcPlaylistTestUtils.getSingleTestData();
    ztvRcFileListTestUtils.initTestData();
    ztvRcFileList = ztvRcFileListTestUtils.getSingleTestData();
    ztvRcCommandTestUtils.initTestData();

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
  void close() {
    httpClientUtilsMock.close();
  }

  /** Executes a command in the ZTV Player and return it's status. */
  @Test
  void executeTest() throws Exception {
    setupInputStreamMock("ztvrc/ztv-rc-status.json");
    ZtvRcCommand ztvRcCommand = ztvRcCommandTestUtils.getSingleTestData();

    ZtvRcStatus returnedZtvRcStatus = ztvPlayer.execute(ztvRcCommand);

    ztvRcStatusTestUtils.assertEqualsAllAttributes(expectedZtvRcStatus, returnedZtvRcStatus);
  }

  /** Executes a command in the ZTV Player and return it's status. */
  @Test
  void executeCommandWithAllParametersTest() throws Exception {
    setupInputStreamMock("ztvrc/ztv-rc-status-equalizer.json");
    ZtvRcCommand commandWithAllParameters = ztvRcCommandTestUtils.getTestDataList().get(1);

    ZtvRcStatus ztvRcStatus = ztvPlayer.execute(commandWithAllParameters);

    assertEquals(null, ztvRcStatus.getAspectRatio());
    assertTrue(!ztvRcStatus.getFullscreen());
    assertEquals(0, ztvRcStatus.getStats().getDisplayedPictures());
    assertEquals("1 - Winter Is Coming.avi", ztvRcStatus.getInformation().getMeta().getFilename());
  }

  /** Executes a command in the ZTV Player with an invalid requestUrl. */
  @Test
  void executeNullCommandTest() {
    ZtvRcStatus ztvRcStatus = ztvPlayer.execute(new ZtvRcCommand());

    assertEquals(null, ztvRcStatus);
  }

  /** Gets the status information of the ZTV Player. */
  @Test
  void getZtvRcStatusTest() throws Exception {
    setupInputStreamMock("ztvrc/ztv-rc-status.json");

    ZtvRcStatus returnedZtvRcStatus = ztvPlayer.getZtvRcStatus();

    ztvRcStatusTestUtils.assertEqualsAllAttributes(expectedZtvRcStatus, returnedZtvRcStatus);
  }

  /** Gets the current playlist of the ZTV Player. */
  @Test
  void getZtvRcPlaylistTest() throws Exception {
    setupInputStreamMock("ztvrc/ztv-rc-playlist.json");

    List<ZtvRcPlaylistItem> returnedPlaylist = ztvPlayer.getPlaylist();

    ztvRcPlaylistTestUtils.assertEqualsAllAttributes(ztvRcPlaylist, returnedPlaylist);
  }

  /** Browses files in the ZTV Player server. */
  @Test
  void browseTest() throws Exception {
    setupInputStreamMock("ztvrc/ztv-rc-filelist.json");

    List<ZtvRcFileListItem> returnedFilelist = ztvPlayer.browse("C:/");

    ztvRcFileListTestUtils.assertEqualsAllAttributes(ztvRcFileList, returnedFilelist);
  }

  /** Browses files in the ZTV Player server. */
  @Test
  void browseEmptyParameterTest() throws Exception {
    setupInputStreamMock("ztvrc/ztv-rc-filelist.json");

    List<ZtvRcFileListItem> returnedFilelist = ztvPlayer.browse(null);

    ztvRcFileListTestUtils.assertEqualsAllAttributes(ztvRcFileList, returnedFilelist);
  }

  /** Setup input stream mock from files in test resources. */
  private void setupInputStreamMock(String resourceName) throws Exception {
    InputStream inputStream = ZtvPlayerTestUtils.getInputStream(resourceName);
    when(HttpClientUtils.getInputStream(any())).thenReturn(inputStream);
  }
}
