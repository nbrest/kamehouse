package com.nicobrest.kamehouse.ztvrc.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcCommand;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcFileListItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import com.nicobrest.kamehouse.ztvrc.testutils.ZtvRcCommandTestUtils;
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

/**
 * Test class for the ZtvRcService.
 *
 * @author nbrest
 */
class ZtvRcServiceTest {

  private ZtvRcStatusTestUtils ztvRcStatusTestUtils = new ZtvRcStatusTestUtils();
  private ZtvRcPlaylistTestUtils ztvRcPlaylistTestUtils = new ZtvRcPlaylistTestUtils();
  private ZtvRcFileListTestUtils ztvRcFileListTestUtils = new ZtvRcFileListTestUtils();
  private ZtvRcCommandTestUtils ztvRcCommandTestUtils = new ZtvRcCommandTestUtils();
  private ZtvRcStatus ztvRcStatus;
  private List<ZtvRcPlaylistItem> ztvRcPlaylist;
  private List<ZtvRcFileListItem> ztvRcFileList;

  @InjectMocks private ZtvRcService ztvRcService;

  @Mock private ZtvPlayerService ztvPlayerService;

  @Mock(name = "ztvPlayer")
  private ZtvPlayer ztvPlayer;

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
    ztvRcCommandTestUtils.initTestData();

    MockitoAnnotations.openMocks(this);
    Mockito.reset(ztvPlayer);
    Mockito.reset(ztvPlayerService);

    when(ztvPlayerService.getByHostname(any())).thenReturn(ztvPlayer);
  }

  /** Tests getting the status information of the ZTV Player passed through the URL. */
  @Test
  void getZtvRcStatusTest() {
    when(ztvPlayer.getZtvRcStatus()).thenReturn(ztvRcStatus);

    ZtvRcStatus returnedStatus = ztvRcService.getZtvRcStatus("kamehouse-server");

    ztvRcStatusTestUtils.assertEqualsAllAttributes(ztvRcStatus, returnedStatus);
    verify(ztvPlayer, times(1)).getZtvRcStatus();
  }

  /** Tests Executing a command in the selected ZTV Player. */
  @Test
  void executeCommandTest() {
    when(ztvPlayer.execute(any())).thenReturn(ztvRcStatus);
    ZtvRcCommand ztvRcCommand = ztvRcCommandTestUtils.getSingleTestData();

    ZtvRcStatus returnedStatus = ztvRcService.execute(ztvRcCommand, "kamehouse-server");

    ztvRcStatusTestUtils.assertEqualsAllAttributes(ztvRcStatus, returnedStatus);
    verify(ztvPlayer, times(1)).execute(any());
  }

  /** Tests getting the playlist from the ZTV Player. */
  @Test
  void getPlaylistTest() {
    when(ztvPlayer.getPlaylist()).thenReturn(ztvRcPlaylist);

    List<ZtvRcPlaylistItem> returnedPlaylist = ztvRcService.getPlaylist("kamehouse-server");

    ztvRcPlaylistTestUtils.assertEqualsAllAttributes(ztvRcPlaylist, returnedPlaylist);
    verify(ztvPlayer, times(1)).getPlaylist();
  }

  /** Tests browsing files in the ZTV Player. */
  @Test
  void browseTest() {
    when(ztvPlayer.browse(any())).thenReturn(ztvRcFileList);

    List<ZtvRcFileListItem> returnedFiles = ztvRcService.browse(null, "kamehouse-server");

    ztvRcFileListTestUtils.assertEqualsAllAttributes(ztvRcFileList, returnedFiles);
    verify(ztvPlayer, times(1)).browse(any());
  }
}
