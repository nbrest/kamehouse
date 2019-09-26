package com.nicobrest.kamehouse.vlcrc.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
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
import java.util.Map;

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
  private VlcRcStatus vlcRcStatus;
  private List<Map<String, Object>> vlcRcPlaylist;
  private List<Map<String, Object>> vlcRcFileList;

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

    assertEquals(vlcRcStatus.getInformation().getTitle(), returnedStatus.getInformation()
        .getTitle());
    //TODO verify all attributes with test utils.
    verify(vlcPlayer, times(1)).getVlcRcStatus();
  }

  /**
   * Tests Executing a command in the selected VLC Player.
   */
  @Test
  public void executeCommandTest() {
    when(vlcPlayer.execute(any())).thenReturn(vlcRcStatus);
    VlcRcCommand vlcRcCommand = new VlcRcCommand();
    vlcRcCommand.setName("fullscreen");

    VlcRcStatus returnedStatus = vlcRcService.execute(vlcRcCommand, "niko-nba");

    assertEquals(vlcRcStatus.getInformation().getTitle(), returnedStatus.getInformation()
        .getTitle());
    // TODO verify all attributes with test utils.
    verify(vlcPlayer, times(1)).execute(any());
  }

  /**
   * Tests getting the playlist from the VLC Player.
   */
  @Test
  public void getPlaylistTest() {
    when(vlcPlayer.getPlaylist()).thenReturn(vlcRcPlaylist);

    List<Map<String, Object>> returnedPlaylist = vlcRcService.getPlaylist("niko-nba");

    assertEquals(2, returnedPlaylist.size());
    // TODO verify all attributes with test utils.
    assertEquals(vlcRcPlaylist.get(0).get("name"), returnedPlaylist.get(0).get("name"));
    assertEquals(vlcRcPlaylist.get(1).get("name"), returnedPlaylist.get(1).get("name"));
    verify(vlcPlayer, times(1)).getPlaylist();
  }

  /**
   * Tests browsing files in the VLC Player.
   */
  @Test
  public void browseTest() {
    when(vlcPlayer.browse(any())).thenReturn(vlcRcFileList);

    List<Map<String, Object>> returnedFilelist = vlcRcService.browse(null, "niko-nba");

    assertEquals(2, returnedFilelist.size());
    //TODO verify all attributes with test utils.
    assertEquals(vlcRcFileList.get(0).get("name"), returnedFilelist.get(0).get("name"));
    assertEquals(vlcRcFileList.get(0).get("type"), returnedFilelist.get(0).get("type"));
    assertEquals(vlcRcFileList.get(0).get("uri"), returnedFilelist.get(0).get("uri"));
    assertEquals(vlcRcFileList.get(1).get("name"), returnedFilelist.get(1).get("name"));
    assertEquals(vlcRcFileList.get(1).get("type"), returnedFilelist.get(1).get("type"));
    assertEquals(vlcRcFileList.get(1).get("uri"), returnedFilelist.get(1).get("uri"));
    verify(vlcPlayer, times(1)).browse(any());
  }
}
