package com.nicobrest.kamehouse.vlcrc.controller;

import static org.hamcrest.Matchers.equalTo; 
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nicobrest.kamehouse.utils.JsonUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus.Equalizer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus.Information;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for the VlcRcController.
 * 
 * @author nbrest
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class VlcRcControllerTest {

  private MockMvc mockMvc;
  private static VlcRcStatus vlcRcStatusMock;
  
  @InjectMocks
  private VlcRcController vlcRcController;

  @Mock(name = "vlcRcService")
  private VlcRcService vlcRcServiceMock;

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
    Equalizer equalizer = vlcRcStatusMock.new Equalizer();
    equalizer.setPreAmp(1);
    vlcRcStatusMock.setEqualizer(equalizer);
    // information
    Information information = vlcRcStatusMock.new Information();
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
  }

  @Before
  public void beforeTest() {
    MockitoAnnotations.initMocks(this);
    Mockito.reset(vlcRcServiceMock);
    mockMvc = MockMvcBuilders.standaloneSetup(vlcRcController).build();
  }

  /**
   * Tests getting the status information of the VLC Player passed through the
   * URL.
   */
  @Test
  public void getVlcRcStatusTest() {

    try {
      when(vlcRcServiceMock.getVlcRcStatus("niko-nba")).thenReturn(vlcRcStatusMock);

      mockMvc.perform(get("/api/v1/vlc-rc/players/niko-nba")).andDo(print()).andExpect(status()
          .isOk()).andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(
              jsonPath("$.apiVersion", equalTo(3))).andExpect(jsonPath("$.videoEffects.saturation",
                  equalTo(1))).andExpect(jsonPath("$.stats.inputBitrate", equalTo(1))).andExpect(
                      jsonPath("$.information.chapter", equalTo("0"))).andExpect(jsonPath(
                          "$.version", equalTo("yukimura")));
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
      when(vlcRcServiceMock.execute(any(), anyString())).thenReturn(vlcRcStatusMock);
      
      mockMvc.perform(post("/api/v1/vlc-rc/players/niko-nba/commands").contentType(
          MediaType.APPLICATION_JSON_UTF8).content(JsonUtils.convertToJsonBytes(vlcRcCommand)))
          .andDo(print()).andExpect(status().isCreated()).andExpect(content().contentType(
              "application/json;charset=UTF-8")).andExpect(jsonPath("$.apiVersion", equalTo(3)))
          .andExpect(jsonPath("$.videoEffects.saturation", equalTo(1))).andExpect(jsonPath(
              "$.stats.inputBitrate", equalTo(1))).andExpect(jsonPath("$.information.chapter",
                  equalTo("0"))).andExpect(jsonPath("$.version", equalTo("yukimura")));

      verify(vlcRcServiceMock, times(1)).execute(any(), anyString());
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown.");
    }
  }
}
