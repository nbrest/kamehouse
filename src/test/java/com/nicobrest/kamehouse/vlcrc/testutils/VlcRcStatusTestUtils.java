package com.nicobrest.kamehouse.vlcrc.testutils;

import com.nicobrest.kamehouse.main.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.main.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus.Equalizer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus.Information;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test data and common test methods to test DragonBallUsers in all layers of
 * the application.
 * 
 * @author nbrest
 *
 */
public class VlcRcStatusTestUtils extends AbstractTestUtils<VlcRcStatus, Object>
    implements TestUtils<VlcRcStatus, Object> {
  
  @Override
  public void initTestData() {
    initSingleTestData();
  }

  private void initSingleTestData() {
    singleTestData = new VlcRcStatus();
    // Direct attributes
    singleTestData.setApiVersion(3);
    singleTestData.setAspectRatio("16:9");
    singleTestData.setAudioDelay(1);
    singleTestData.setCurrentPlId(1);
    singleTestData.setFullscreen(true);
    singleTestData.setLength(1000);
    singleTestData.setLoop(false);
    singleTestData.setPosition(0.1);
    singleTestData.setRandom(false);
    singleTestData.setRate(1);
    singleTestData.setRepeat(false);
    singleTestData.setState("playing");
    singleTestData.setSubtitleDelay(1);
    singleTestData.setTime(1);
    singleTestData.setVersion("yukimura");
    singleTestData.setVolume(1);
    // audio filters
    Map<String, String> audioFilters = new HashMap<String, String>();
    audioFilters.put("filter_0", "");
    singleTestData.setAudioFilters(audioFilters);
    // video effects
    Map<String, Integer> videoEffects = new HashMap<String, Integer>();
    videoEffects.put("saturation", 1);
    videoEffects.put("brightness", 1);
    videoEffects.put("contrast", 1);
    videoEffects.put("hue", 0);
    videoEffects.put("gamma", 1);
    singleTestData.setVideoEffects(videoEffects);
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
    singleTestData.setStats(stats);
    // equalizer
    Equalizer equalizer = new VlcRcStatus.Equalizer();
    equalizer.setPreAmp(1);
    singleTestData.setEqualizer(equalizer);
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
    singleTestData.setInformation(information);
  }
}
