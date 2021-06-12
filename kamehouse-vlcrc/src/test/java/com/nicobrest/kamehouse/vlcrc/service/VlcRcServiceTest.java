package com.nicobrest.kamehouse.vlcrc.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcCommandTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcFileListTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcPlaylistTestUtils;
import com.nicobrest.kamehouse.vlcrc.testutils.VlcRcStatusTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Test class for the VlcRcService.
 * 
 * @author nbrest
 *
 */
public class VlcRcServiceTest {

  private VlcRcStatusTestUtils vlcRcStatusTestUtils = new VlcRcStatusTestUtils();
  private VlcRcPlaylistTestUtils vlcRcPlaylistTestUtils = new VlcRcPlaylistTestUtils();
  private VlcRcFileListTestUtils vlcRcFileListTestUtils = new VlcRcFileListTestUtils();
  private VlcRcCommandTestUtils vlcRcCommandTestUtils = new VlcRcCommandTestUtils();
  private VlcRcStatus vlcRcStatus;
  private List<VlcRcPlaylistItem> vlcRcPlaylist;
  private List<VlcRcFileListItem> vlcRcFileList;

  @InjectMocks
  private VlcRcService vlcRcService;

  @Mock
  private VlcPlayerService vlcPlayerService;

  @Mock(name = "vlcPlayer")
  private VlcPlayer vlcPlayer;

  @Before
  public void beforeTest() {
    vlcRcStatusTestUtils.initTestData();
    vlcRcStatus = vlcRcStatusTestUtils.getSingleTestData();
    vlcRcPlaylistTestUtils.initTestData();
    vlcRcPlaylist = vlcRcPlaylistTestUtils.getSingleTestData();
    vlcRcFileListTestUtils.initTestData();
    vlcRcFileList = vlcRcFileListTestUtils.getSingleTestData();
    vlcRcCommandTestUtils.initTestData();
    
    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcPlayer);
    Mockito.reset(vlcPlayerService);

    when(vlcPlayerService.getByHostname(any())).thenReturn(vlcPlayer);
  }

  /**
   * Tests getting the status information of the VLC Player passed through the
   * URL.
   */
  @Test
  public void getVlcRcStatusTest() {
    when(vlcPlayer.getVlcRcStatus()).thenReturn(vlcRcStatus);

    VlcRcStatus returnedStatus = vlcRcService.getVlcRcStatus("niko-nba");

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedStatus);
    verify(vlcPlayer, times(1)).getVlcRcStatus();
  }

  /**
   * Tests Executing a command in the selected VLC Player.
   */
  @Test
  public void executeCommandTest() {
    when(vlcPlayer.execute(any())).thenReturn(vlcRcStatus);
    VlcRcCommand vlcRcCommand = vlcRcCommandTestUtils.getSingleTestData();

    VlcRcStatus returnedStatus = vlcRcService.execute(vlcRcCommand, "niko-nba");

    vlcRcStatusTestUtils.assertEqualsAllAttributes(vlcRcStatus, returnedStatus);
    verify(vlcPlayer, times(1)).execute(any());
  }

  /**
   * Tests getting the playlist from the VLC Player.
   */
  @Test
  public void getPlaylistTest() {
    when(vlcPlayer.getPlaylist()).thenReturn(vlcRcPlaylist);

    List<VlcRcPlaylistItem> returnedPlaylist = vlcRcService.getPlaylist("niko-nba");

    vlcRcPlaylistTestUtils.assertEqualsAllAttributes(vlcRcPlaylist, returnedPlaylist);
    verify(vlcPlayer, times(1)).getPlaylist();
  }

  /**
   * Tests browsing files in the VLC Player.
   */
  @Test
  public void browseTest() {
    when(vlcPlayer.browse(any())).thenReturn(vlcRcFileList);

    List<VlcRcFileListItem> returnedFilelist = vlcRcService.browse(null, "niko-nba");

    vlcRcFileListTestUtils.assertEqualsAllAttributes(vlcRcFileList, returnedFilelist);
    verify(vlcPlayer, times(1)).browse(any());
  }
}
