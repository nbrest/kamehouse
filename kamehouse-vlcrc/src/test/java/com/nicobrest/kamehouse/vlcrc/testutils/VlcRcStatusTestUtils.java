package com.nicobrest.kamehouse.vlcrc.testutils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nicobrest.kamehouse.commons.testutils.AbstractTestUtils;
import com.nicobrest.kamehouse.commons.testutils.TestUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Test data and common test methods to test VlcRcStatus.
 *
 * @author nbrest
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
    assertVideoEffects(expected, returned);
    assertStats(expected, returned);
    // Equalizer
    VlcRcStatus.Equalizer expectedEqualizer = expected.getEqualizer();
    VlcRcStatus.Equalizer returnedEqualizer = returned.getEqualizer();
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

  /** Assert root attributes of VlcRcStatus. */
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

  /** Assert stats of VlcRcStatus. */
  private void assertStats(VlcRcStatus expected, VlcRcStatus returned) {
    VlcRcStatus.Stats expectedStats = expected.getStats();
    VlcRcStatus.Stats returnedStats = returned.getStats();
    if (expectedStats != null && returnedStats != null) {
      assertEquals(expectedStats.getInputBitrate(), returnedStats.getInputBitrate());
      assertEquals(expectedStats.getSentBytes(), returnedStats.getSentBytes());
      assertEquals(expectedStats.getLostaBuffers(), returnedStats.getLostaBuffers());
      assertEquals(expectedStats.getAverageDemuxBitrate(), returnedStats.getAverageDemuxBitrate());
      assertEquals(expectedStats.getReadPackets(), returnedStats.getReadPackets());
      assertEquals(expectedStats.getDemuxReadPackets(), returnedStats.getDemuxReadPackets());
      assertEquals(expectedStats.getLostPictures(), returnedStats.getLostPictures());
      assertEquals(expectedStats.getDisplayedPictures(), returnedStats.getDisplayedPictures());
      assertEquals(expectedStats.getSentPackets(), returnedStats.getSentPackets());
      assertEquals(expectedStats.getDemuxReadBytes(), returnedStats.getDemuxReadBytes());
      assertEquals(expectedStats.getDemuxBitrate(), returnedStats.getDemuxBitrate());
      assertEquals(expectedStats.getPlayedaBuffers(), returnedStats.getPlayedaBuffers());
      assertEquals(expectedStats.getDemuxDiscontinuity(), returnedStats.getDemuxDiscontinuity());
      assertEquals(expectedStats.getDecodedAudio(), returnedStats.getDecodedAudio());
      assertEquals(expectedStats.getSendBitrate(), returnedStats.getSendBitrate());
      assertEquals(expectedStats.getReadBytes(), returnedStats.getReadBytes());
      assertEquals(expectedStats.getAverageInputBitrate(), returnedStats.getAverageInputBitrate());
      assertEquals(expectedStats.getDemuxCorrupted(), returnedStats.getDemuxCorrupted());
      assertEquals(expectedStats.getDecodedVideo(), returnedStats.getDecodedVideo());
    } else {
      // Check they are both null
      assertEquals(expectedStats, returnedStats);
    }
  }

  /** Assert video effects of VlcRcStatus. */
  private void assertVideoEffects(VlcRcStatus expectedEntity, VlcRcStatus returnedEntity) {
    VlcRcStatus.VideoEffects expected = expectedEntity.getVideoEffects();
    VlcRcStatus.VideoEffects returned = returnedEntity.getVideoEffects();
    if (expected != null && returned != null) {
      assertEquals(expected.getBrightness(), returned.getBrightness());
      assertEquals(expected.getContrast(), returned.getContrast());
      assertEquals(expected.getGamma(), returned.getGamma());
      assertEquals(expected.getHue(), returned.getHue());
      assertEquals(expected.getSaturation(), returned.getSaturation());
    } else {
      // Check they are both null
      assertEquals(expected, returned);
    }
  }

  /** Assert information attribute of VlcRcStatus. */
  private void assertInformation(VlcRcStatus expected, VlcRcStatus returned) {
    VlcRcStatus.Information expectedInformation = expected.getInformation();
    VlcRcStatus.Information returnedInformation = returned.getInformation();
    if (expectedInformation != null && returnedInformation != null) {
      assertEquals(expectedInformation.getChapter(), returnedInformation.getChapter());
      assertThat(returnedInformation.getChapters(), is(expectedInformation.getChapters()));
      assertEquals(expectedInformation.getTitle(), returnedInformation.getTitle());
      assertThat(returnedInformation.getTitles(), is(expectedInformation.getTitles()));
      assertMeta(expectedInformation, returnedInformation);
      assertAudio(expectedInformation, returnedInformation);
      assertVideo(expectedInformation, returnedInformation);
      assertSubtitle(expectedInformation, returnedInformation);
    } else {
      // Check that they are both null
      assertEquals(expectedInformation, returnedInformation);
    }
  }

  /** Assert meta attribute of information of VlcRcStatus. */
  private void assertMeta(
      VlcRcStatus.Information expectedInformation, VlcRcStatus.Information returnedInformation) {
    VlcRcStatus.Information.Meta expected = expectedInformation.getMeta();
    VlcRcStatus.Information.Meta returned = returnedInformation.getMeta();
    if (expected != null && returned != null) {
      assertEquals(expected.getArtist(), returned.getArtist());
      assertEquals(expected.getArtworkUrl(), returned.getArtworkUrl());
      assertEquals(expected.getFilename(), returned.getFilename());
      assertEquals(expected.getName(), returned.getName());
      assertEquals(expected.getSetting(), returned.getSetting());
      assertEquals(expected.getSoftware(), returned.getSoftware());
      assertEquals(expected.getTitle(), returned.getTitle());
    } else {
      // Check they are both null
      assertEquals(expected, returned);
    }
  }

  /** Assert audio attribute of information of VlcRcStatus. */
  private void assertAudio(
      VlcRcStatus.Information expectedInformation, VlcRcStatus.Information returnedInformation) {
    VlcRcStatus.Information.Audio expected = expectedInformation.getAudio();
    VlcRcStatus.Information.Audio returned = returnedInformation.getAudio();
    if (expected != null && returned != null) {
      assertEquals(expected.getBitrate(), returned.getBitrate());
      assertEquals(expected.getChannels(), returned.getChannels());
      assertEquals(expected.getCodec(), returned.getCodec());
      assertEquals(expected.getLanguage(), returned.getLanguage());
      assertEquals(expected.getName(), returned.getName());
      assertEquals(expected.getSampleRate(), returned.getSampleRate());
      assertEquals(expected.getType(), returned.getType());
    } else {
      // Check they are both null
      assertEquals(expected, returned);
    }
  }

  /** Assert video attribute of information of VlcRcStatus. */
  private void assertVideo(
      VlcRcStatus.Information expectedInformation, VlcRcStatus.Information returnedInformation) {
    VlcRcStatus.Information.Video expected = expectedInformation.getVideo();
    VlcRcStatus.Information.Video returned = returnedInformation.getVideo();
    if (expected != null && returned != null) {
      assertEquals(expected.getCodec(), returned.getCodec());
      assertEquals(expected.getLanguage(), returned.getLanguage());
      assertEquals(expected.getName(), returned.getName());
      assertEquals(expected.getType(), returned.getType());
      assertEquals(expected.getDecodedFormat(), returned.getDecodedFormat());
      assertEquals(expected.getDisplayResolution(), returned.getDisplayResolution());
      assertEquals(expected.getFrameRate(), returned.getFrameRate());
      assertEquals(expected.getResolution(), returned.getResolution());
    } else {
      // Check they are both null
      assertEquals(expected, returned);
    }
  }

  /** Assert subtitle attribute of information of VlcRcStatus. */
  private void assertSubtitle(
      VlcRcStatus.Information expectedInformation, VlcRcStatus.Information returnedInformation) {
    VlcRcStatus.Information.Subtitle expected = expectedInformation.getSubtitle();
    VlcRcStatus.Information.Subtitle returned = returnedInformation.getSubtitle();
    if (expected != null && returned != null) {
      assertEquals(expected.getCodec(), returned.getCodec());
      assertEquals(expected.getLanguage(), returned.getLanguage());
      assertEquals(expected.getName(), returned.getName());
      assertEquals(expected.getType(), returned.getType());
    } else {
      // Check they are both null
      assertEquals(expected, returned);
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
    singleTestData.setPosition(0.11406684666872);
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
    VlcRcStatus.VideoEffects videoEffects = new VlcRcStatus.VideoEffects();
    videoEffects.setSaturation(1);
    videoEffects.setBrightness(1);
    videoEffects.setContrast(1);
    videoEffects.setHue(0);
    videoEffects.setGamma(1);
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
    VlcRcStatus.Equalizer equalizer = new VlcRcStatus.Equalizer();
    equalizer.setPreAmp(0);
    singleTestData.setEqualizer(equalizer);
    // information
    VlcRcStatus.Information information = new VlcRcStatus.Information();
    information.setChapter("0");
    information.setChapters(Arrays.asList(""));
    information.setTitle("0");
    information.setTitles(Arrays.asList(""));
    // Information Video
    VlcRcStatus.Information.Video video = new VlcRcStatus.Information.Video();
    video.setName("Stream 0");
    video.setType("Video");
    video.setFrameRate("23.976024");
    video.setDecodedFormat("Planar 4:2:0 YUV");
    video.setDisplayResolution(null);
    video.setCodec("Xvid MPEG-4 Video (XVID)");
    video.setResolution(null);
    video.setLanguage(null);
    information.setVideo(video);
    // Information Audio
    VlcRcStatus.Information.Audio audio = new VlcRcStatus.Information.Audio();
    audio.setName("Stream 1");
    audio.setType("Audio");
    audio.setBitrate("128 kb/s");
    audio.setChannels("Stereo");
    audio.setSampleRate("48000 Hz");
    audio.setCodec("MPEG Audio layer 3 (mp3 )");
    audio.setLanguage(null);
    information.setAudio(audio);
    // Information Meta
    VlcRcStatus.Information.Meta meta = new VlcRcStatus.Information.Meta();
    meta.setName("meta");
    meta.setFilename("1 - Winter Is Coming.avi");
    meta.setTitle("Winter Is Coming.avi");
    meta.setArtist("1");
    meta.setArtworkUrl(
        "file:///C:/Users/nbrest/AppData/Roaming/vlc"
            + "/art/arturl/939adb7ed723657d6a078ce9085e83ab/art");
    meta.setSetting(" HAS_INDEX IS_INTERLEAVED");
    meta.setSoftware("Nandub v1.0rc2");
    information.setMeta(meta);
    // Information Subtitle
    VlcRcStatus.Information.Subtitle subtitle = new VlcRcStatus.Information.Subtitle();
    subtitle.setName("Stream 2");
    subtitle.setType("Subtitle");
    subtitle.setCodec("Text subtitles with various tags (subt)");
    subtitle.setLanguage("\\subs\\1 - Winter Is Coming");
    information.setSubtitle(subtitle);
    singleTestData.setInformation(information);
  }
}
