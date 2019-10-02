package com.nicobrest.kamehouse.vlcrc.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

  @Override
  public void assertEqualsAllAttributes(VlcRcStatus expected, VlcRcStatus returned) {
    assertRootAttributes(expected, returned);
    // AudioFilters
    assertThat(returned.getAudioFilters(), is(expected.getAudioFilters()));
    // VideoEffects
    assertThat(returned.getVideoEffects(), is(expected.getVideoEffects()));
    assertStats(expected, returned);
    // Equalizer
    Equalizer expectedEqualizer = expected.getEqualizer();
    Equalizer returnedEqualizer = returned.getEqualizer();
    if (expectedEqualizer != null && returnedEqualizer != null) {
      assertEquals(expectedEqualizer.getPreAmp(), returnedEqualizer.getPreAmp());
      assertThat(returnedEqualizer.getBands(), is(expectedEqualizer.getBands()));
      assertThat(returnedEqualizer.getPresets(), is(expectedEqualizer.getPresets()));
    } else {
      // Check that they are both null
      assertEquals(expectedEqualizer, returnedEqualizer);
    }
    assertInformation(expected, returned);
  }

  private void assertRootAttributes(VlcRcStatus expected, VlcRcStatus returned) {
    assertEquals(expected.getApiVersion(), returned.getApiVersion());
    assertEquals(expected.getAspectRatio(), returned.getAspectRatio());
    assertEquals(expected.getAudioDelay(), returned.getAudioDelay());
    assertEquals(expected.getCurrentPlId(), returned.getCurrentPlId());
    assertEquals(expected.getFullscreen(), returned.getFullscreen());
    assertEquals(expected.getLength(), returned.getLength());
    assertEquals(expected.getLoop(), returned.getLoop());
    assertEquals(expected.getPosition(), returned.getPosition(), 1);
    assertEquals(expected.getRandom(), returned.getRandom());
    assertEquals(expected.getRate(), returned.getRate());
    assertEquals(expected.getRepeat(), returned.getRepeat());
    assertEquals(expected.getState(), returned.getState());
    assertEquals(expected.getSubtitleDelay(), returned.getSubtitleDelay());
    assertEquals(expected.getTime(), returned.getTime());
    assertEquals(expected.getVersion(), returned.getVersion());
    assertEquals(expected.getVolume(), returned.getVolume());
  }

  private void assertStats(VlcRcStatus expected, VlcRcStatus returned) {
    VlcRcStatus.Stats expectedStats = expected.getStats();
    VlcRcStatus.Stats returnedStats = returned.getStats();
    if (expectedStats != null && returnedStats != null) {
      assertEqualsAsString(expectedStats.getInputBitrate(), returnedStats.getInputBitrate());
      assertEqualsAsString(expectedStats.getSentBytes(), returnedStats.getSentBytes());
      assertEqualsAsString(expectedStats.getLostaBuffers(), returnedStats.getLostaBuffers());
      assertEqualsAsString(expectedStats.getAverageDemuxBitrate(),
          returnedStats.getAverageDemuxBitrate());
      assertEqualsAsString(expectedStats.getReadPackets(), returnedStats.getReadPackets());
      assertEqualsAsString(expectedStats.getDemuxReadPackets(),
          returnedStats.getDemuxReadPackets());
      assertEqualsAsString(expectedStats.getLostPictures(), returnedStats.getLostPictures());
      assertEqualsAsString(expectedStats.getDisplayedPictures(),
          returnedStats.getDisplayedPictures());
      assertEqualsAsString(expectedStats.getSentPackets(), returnedStats.getSentPackets());
      assertEqualsAsString(expectedStats.getDemuxReadBytes(), returnedStats.getDemuxReadBytes());
      assertEqualsAsString(expectedStats.getDemuxBitrate(), returnedStats.getDemuxBitrate());
      assertEqualsAsString(expectedStats.getPlayedaBuffers(), returnedStats.getPlayedaBuffers());
      assertEqualsAsString(expectedStats.getDemuxDiscontinuity(),
          returnedStats.getDemuxDiscontinuity());
      assertEqualsAsString(expectedStats.getDecodedAudio(), returnedStats.getDecodedAudio());
      assertEqualsAsString(expectedStats.getSendBitrate(), returnedStats.getSendBitrate());
      assertEqualsAsString(expectedStats.getReadBytes(), returnedStats.getReadBytes());
      assertEqualsAsString(expectedStats.getAverageInputBitrate(),
          returnedStats.getAverageInputBitrate());
      assertEqualsAsString(expectedStats.getDemuxCorrupted(), returnedStats.getDemuxCorrupted());
      assertEqualsAsString(expectedStats.getDecodedVideo(), returnedStats.getDecodedVideo());
    } else {
      // Check they are both null
      assertEquals(expectedStats, returnedStats);
    }
  }

  private void assertInformation(VlcRcStatus expected, VlcRcStatus returned) {
    Information expectedInformation = expected.getInformation();
    Information returnedInformation = returned.getInformation();
    if (expectedInformation != null && returnedInformation != null) {
      assertEquals(expectedInformation.getChapter(), returnedInformation.getChapter());
      assertThat(returnedInformation.getChapters(), is(expectedInformation.getChapters()));
      assertEquals(expectedInformation.getTitle(), returnedInformation.getTitle());
      assertThat(returnedInformation.getTitles(), is(expectedInformation.getTitles()));
      List<Map<String, Object>> expectedCategoryList = expectedInformation.getCategory();
      List<Map<String, Object>> returnedCategoryList = returnedInformation.getCategory();
      if (expectedCategoryList != null && returnedCategoryList != null) {
        assertEquals(expectedCategoryList.size(), returnedCategoryList.size());
        for (int i = 0; i < expectedCategoryList.size(); i++) {
          assertEquals(expectedCategoryList.get(i), returnedCategoryList.get(i));
        }
      } else {
        assertEquals(expectedCategoryList, returnedCategoryList);
      }
      assertThat(returnedInformation.getCategory(), is(expectedInformation.getCategory()));
    } else {
      // Check that they are both null
      assertEquals(expectedInformation, returnedInformation);
    }
  }

  private void initSingleTestData() {
    // Mapped to the contents of test/resources/vlcrc/vlc-rc-status.json
    singleTestData = new VlcRcStatus();
    // Direct attributes
    singleTestData.setApiVersion(3);
    singleTestData.setAspectRatio("16:9");
    singleTestData.setAudioDelay(0);
    singleTestData.setCurrentPlId(4);
    singleTestData.setFullscreen(true);
    singleTestData.setLength(3695);
    singleTestData.setLoop(false);
    singleTestData.setPosition(0.1);
    singleTestData.setRandom(false);
    singleTestData.setRate(1);
    singleTestData.setRepeat(false);
    singleTestData.setState("playing");
    singleTestData.setSubtitleDelay(0);
    singleTestData.setTime(421);
    singleTestData.setVersion("3.0.4 Vetinari");
    singleTestData.setVolume(256);
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
    VlcRcStatus.Stats stats = new VlcRcStatus.Stats();
    stats.setInputBitrate(0.085485093295574);
    stats.setSentBytes(0);
    stats.setLostaBuffers(0);
    stats.setAverageDemuxBitrate(0D);
    stats.setReadPackets(4286);
    stats.setDemuxReadPackets(0);
    stats.setLostPictures(18);
    stats.setDisplayedPictures(1988);
    stats.setSentPackets(0);
    stats.setDemuxReadBytes(55350686);
    stats.setDemuxBitrate(0.096156001091003);
    stats.setPlayedaBuffers(17618);
    stats.setDemuxDiscontinuity(4);
    stats.setDecodedAudio(35587);
    stats.setSendBitrate(0D);
    stats.setReadBytes(59057789);
    stats.setAverageInputBitrate(0D);
    stats.setDemuxCorrupted(0);
    stats.setDecodedVideo(12263);
    singleTestData.setStats(stats);
    // equalizer
    Equalizer equalizer = new VlcRcStatus.Equalizer();
    equalizer.setPreAmp(0);
    singleTestData.setEqualizer(equalizer);
    // information
    Information information = new VlcRcStatus.Information();
    information.setChapter("0");
    information.setChapters(Arrays.asList(""));
    information.setTitle("0");
    information.setTitles(Arrays.asList(""));
    List<Map<String, Object>> informationCategories = new ArrayList<Map<String, Object>>();
    Map<String, Object> stream0 = new HashMap<String, Object>();
    stream0.put("name", "Stream 0");
    stream0.put("type", "Video");
    stream0.put("frameRate", "23.976024");
    stream0.put("decodedFormat", "Planar 4:2:0 YUV");
    stream0.put("displayResolution", null);
    stream0.put("codec", "Xvid MPEG-4 Video (XVID)");
    stream0.put("resolution", null);
    informationCategories.add(stream0);
    Map<String, Object> stream1 = new HashMap<String, Object>();
    stream1.put("name", "Stream 1");
    stream1.put("type", "Audio");
    stream1.put("bitrate", "128 kb/s");
    stream1.put("channels", "Stereo");
    stream1.put("sampleRate", "48000 Hz");
    stream1.put("codec", "MPEG Audio layer 3 (mp3 )");
    informationCategories.add(stream1);
    Map<String, Object> meta = new HashMap<String, Object>();
    meta.put("name", "meta");
    meta.put("filename", "1 - Winter Is Coming.avi");
    meta.put("title", "Winter Is Coming.avi");
    meta.put("artist", "1");
    meta.put("setting", " HAS_INDEX IS_INTERLEAVED");
    meta.put("software", "Nandub v1.0rc2");
    informationCategories.add(meta);
    Map<String, Object> stream2 = new HashMap<String, Object>();
    stream2.put("name", "Stream 2");
    stream2.put("type", "Subtitle");
    stream2.put("codec", "Text subtitles with various tags (subt)");
    stream2.put("language", "\\subs\\1 - Winter Is Coming");
    informationCategories.add(stream2);
    information.setCategory(informationCategories);
    singleTestData.setInformation(information);
  }
}
