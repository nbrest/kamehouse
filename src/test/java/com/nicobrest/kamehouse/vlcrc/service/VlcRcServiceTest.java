package com.nicobrest.kamehouse.vlcrc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus.Equalizer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus.Information;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for the VlcRcService.
 * 
 * @author nbrest
 *
 */
public class VlcRcServiceTest {

  private static VlcRcStatus vlcRcStatusMock;
  private static List<Map<String, Object>> vlcRcPlaylistMock;
  private static List<Map<String, Object>> vlcRcFilelistMock;

  @InjectMocks
  private VlcRcService vlcRcService;

  @Mock
  private VlcPlayerService vlcPlayerService;
  
  @Mock(name = "vlcPlayer")
  private VlcPlayer vlcPlayer;

  @BeforeClass
  public static void beforeClassTest() {
    vlcRcStatusMock = new VlcRcStatus();
    // Direct attributes
    vlcRcStatusMock.setApiVersion(3);
    vlcRcStatusMock.setAspectRatio("16:9");
    vlcRcStatusMock.setAudioDelay(1);
    vlcRcStatusMock.setCurrentPlId(1);
    vlcRcStatusMock.setFullscreen(true);
    vlcRcStatusMock.setLength(1000);
    vlcRcStatusMock.setLoop(false);
    vlcRcStatusMock.setPosition(0.1);
    vlcRcStatusMock.setRandom(false);
    vlcRcStatusMock.setRate(1);
    vlcRcStatusMock.setRepeat(false);
    vlcRcStatusMock.setState("playing");
    vlcRcStatusMock.setSubtitleDelay(1);
    vlcRcStatusMock.setTime(1);
    vlcRcStatusMock.setVersion("yukimura");
    vlcRcStatusMock.setVolume(1);
    // audio filters
    Map<String, String> audioFilters = new HashMap<String, String>();
    audioFilters.put("filter_0", "");
    vlcRcStatusMock.setAudioFilters(audioFilters);
    // video effects
    Map<String, Integer> videoEffects = new HashMap<String, Integer>();
    videoEffects.put("saturation", 1);
    videoEffects.put("brightness", 1);
    videoEffects.put("contrast", 1);
    videoEffects.put("hue", 0);
    videoEffects.put("gamma", 1);
    vlcRcStatusMock.setVideoEffects(videoEffects);
    // stats
    Map<String, Object> stats = new HashMap<String, Object>();
    stats.put("inputBitrate", 1);
    stats.put("sentBytes", 1);
    stats.put("lostaBuffers", 1);
    stats.put("averageDemuxBitrate", 1);
    stats.put("readPackets", 1);
    stats.put("demuxReadPackets", 1);
    stats.put("lostPictures", 1);
    stats.put("displayedPictures", 1);
    stats.put("sentPackets", 1);
    stats.put("demuxReadBytes", 1);
    stats.put("demuxBitrate", 1);
    stats.put("playedaBuffers", 1);
    stats.put("demuxDiscontinuity", 1);
    stats.put("decodedAudio", 1);
    stats.put("sendBitrate", 1);
    stats.put("readBytes", 1);
    stats.put("averageInputBitrate", 1);
    stats.put("demuxCorrupted", 1);
    stats.put("decodedVideo", 1);
    vlcRcStatusMock.setStats(stats);
    // equalizer
    Equalizer equalizer = new VlcRcStatus.Equalizer();
    equalizer.setPreAmp(1);
    vlcRcStatusMock.setEqualizer(equalizer);
    // information
    Information information = new VlcRcStatus.Information();
    information.setChapter("0");
    information.setChapters(Arrays.asList(""));
    information.setChapter("0");
    information.setChapters(Arrays.asList(""));
    List<Map<String, Object>> informationCategories = new ArrayList<Map<String, Object>>();
    Map<String, Object> meta = new HashMap<String, Object>();
    meta.put("name", "goku-name");
    meta.put("filename", "goku-filename");
    meta.put("title", "goku-title");
    meta.put("artist", "goku-artist");
    meta.put("setting", "goku-setting");
    meta.put("software", "goku-software");
    informationCategories.add(meta);
    Map<String, Object> video = new HashMap<String, Object>();
    video.put("name", "goku-name");
    video.put("type", "goku-type");
    video.put("frameRate", "goku-frameRate");
    video.put("decodedFormat", "goku-decodedFormat");
    video.put("displayResolution", "goku-displayResolution");
    video.put("codec", "goku-codec");
    video.put("language", "goku-language");
    video.put("resolution", "goku-resolution");
    informationCategories.add(video);
    Map<String, Object> audio = new HashMap<String, Object>();
    audio.put("name", "goku-name");
    audio.put("type", "goku-type");
    audio.put("bitrate", "goku-bitrate");
    audio.put("channels", "goku-channels");
    audio.put("sampleRate", "goku-sampleRate");
    audio.put("codec", "goku-codec");
    audio.put("language", "goku-language");
    informationCategories.add(audio);
    Map<String, Object> subtitle = new HashMap<String, Object>();
    subtitle.put("name", "goku-name");
    subtitle.put("type", "goku-type");
    subtitle.put("codec", "goku-codec");
    subtitle.put("language", "goku-language");
    informationCategories.add(subtitle);
    information.setCategory(informationCategories);
    vlcRcStatusMock.setInformation(information);

    vlcRcPlaylistMock = new ArrayList<Map<String, Object>>();
    Map<String, Object> playlistItem1 = new HashMap<String, Object>();
    playlistItem1.put("id", 1);
    playlistItem1.put("name", "Lleyton Hewitt- Brash teenager to Aussie great.mp4");
    playlistItem1.put("uri", "file:///home/nbrest/Videos/Lleyton%20"
        + "Hewitt-%20Brash%20teenager%20to%20Aussie%20great.mp4");
    playlistItem1.put("duration", 281);
    vlcRcPlaylistMock.add(playlistItem1);
    Map<String, Object> playlistItem2 = new HashMap<String, Object>();
    playlistItem2.put("id", 2);
    playlistItem2.put("name", "Lleyton Hewitt Special.mp4");
    playlistItem2.put("uri", "file:///home/nbrest/Videos/Lleyton%20Hewitt%20Special.mp4");
    playlistItem2.put("duration", 325);
    vlcRcPlaylistMock.add(playlistItem2);

    vlcRcFilelistMock = new ArrayList<Map<String, Object>>();
    Map<String, Object> fileListItem1 = new HashMap<String, Object>();
    fileListItem1.put("type", "dir");
    fileListItem1.put("path", "C:\\");
    fileListItem1.put("name", "C:\\");
    fileListItem1.put("uri", "file:///C:/");
    fileListItem1.put("accessTime", 315543600);
    fileListItem1.put("uid", 0);
    fileListItem1.put("creationTime", 315543600);
    fileListItem1.put("gid", 0);
    fileListItem1.put("modificationTime", 315543600);
    fileListItem1.put("mode", 16895);
    fileListItem1.put("size", 0);
    vlcRcFilelistMock.add(fileListItem1);
    Map<String, Object> fileListItem2 = new HashMap<String, Object>();
    fileListItem2.put("type", "dir");
    fileListItem2.put("path", "D:\\");
    fileListItem2.put("name", "D:\\");
    fileListItem2.put("uri", "file:///D:/");
    fileListItem2.put("accessTime", 315543600);
    fileListItem2.put("uid", 0);
    fileListItem2.put("creationTime", 315543600);
    fileListItem2.put("gid", 0);
    fileListItem2.put("modificationTime", 315543600);
    fileListItem2.put("mode", 16895);
    fileListItem2.put("size", 0);
    vlcRcFilelistMock.add(fileListItem2);
  }

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcPlayer);
    Mockito.reset(vlcPlayerService);
  }

  /**
   * Tests getting the status information of the VLC Player passed through the
   * URL.
   */
  @Test
  public void getVlcRcStatusTest() {

    try {
      when(vlcPlayerService.getByHostname(any())).thenReturn(vlcPlayer);
      when(vlcPlayer.getVlcRcStatus()).thenReturn(vlcRcStatusMock);
      VlcRcStatus returnedStatus = vlcRcService.getVlcRcStatus("niko-nba");
      assertEquals(vlcRcStatusMock.getInformation().getTitle(), returnedStatus.getInformation()
          .getTitle());
      verify(vlcPlayer, times(1)).getVlcRcStatus();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Tests Executing a command in the selected VLC Player.
   */
  @Test
  public void executeCommandTest() {

    try {
      VlcRcCommand vlcRcCommand = new VlcRcCommand();
      vlcRcCommand.setName("fullscreen");
      when(vlcPlayerService.getByHostname(any())).thenReturn(vlcPlayer);
      when(vlcPlayer.execute(any())).thenReturn(vlcRcStatusMock);
      VlcRcStatus returnedStatus = vlcRcService.execute(vlcRcCommand, "niko-nba");
      assertEquals(vlcRcStatusMock.getInformation().getTitle(), returnedStatus.getInformation()
          .getTitle());
      verify(vlcPlayer, times(1)).execute(any());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Tests getting the playlist from the VLC Player.
   */
  @Test
  public void getPlaylistTest() {

    try {
      when(vlcPlayerService.getByHostname(any())).thenReturn(vlcPlayer);
      when(vlcPlayer.getPlaylist()).thenReturn(vlcRcPlaylistMock);
      List<Map<String, Object>> returnedPlaylist = vlcRcService.getPlaylist("niko-nba");
      assertEquals(2, returnedPlaylist.size());
      assertEquals(vlcRcPlaylistMock.get(0).get("name"), returnedPlaylist.get(0).get("name"));
      assertEquals(vlcRcPlaylistMock.get(1).get("name"), returnedPlaylist.get(1).get("name"));
      verify(vlcPlayer, times(1)).getPlaylist();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }

  /**
   * Tests browsing files in the VLC Player.
   */
  @Test
  public void browseTest() {

    try {
      when(vlcPlayerService.getByHostname(any())).thenReturn(vlcPlayer);
      when(vlcPlayer.browse(any())).thenReturn(vlcRcFilelistMock);
      List<Map<String, Object>> returnedFilelist = vlcRcService.browse(null, "niko-nba");
      assertEquals(2, returnedFilelist.size());
      assertEquals(vlcRcFilelistMock.get(0).get("name"), returnedFilelist.get(0).get("name"));
      assertEquals(vlcRcFilelistMock.get(0).get("type"), returnedFilelist.get(0).get("type"));
      assertEquals(vlcRcFilelistMock.get(0).get("uri"), returnedFilelist.get(0).get("uri"));
      assertEquals(vlcRcFilelistMock.get(1).get("name"), returnedFilelist.get(1).get("name"));
      assertEquals(vlcRcFilelistMock.get(1).get("type"), returnedFilelist.get(1).get("type"));
      assertEquals(vlcRcFilelistMock.get(1).get("uri"), returnedFilelist.get(1).get("uri"));
      verify(vlcPlayer, times(1)).browse(any());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
