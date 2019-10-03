package com.nicobrest.kamehouse.vlcrc.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.main.utils.JsonUtils;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Builder class to generate a VlcRcStatus object based on an input. The
 * VlcRcStatus class is so complex to build, that it seems right to separate
 * it's creation to a helper class and not have it mixed with the vlc player
 * logic. It could even be a service.
 * 
 * @author nbrest
 *
 */
public class VlcRcStatusBuilder {

  private static final Logger logger = LoggerFactory.getLogger(VlcRcStatusBuilder.class);
  private static final String FILENAME = "filename";
  private static final String ARTIST = "artist";
  private static final String SETTING = "setting";
  private static final String TITLE = "title";
  private static final String CODEC_CC = "Codec";
  private static final String LANGUAGE_CC = "Language";
  private static final String TYPE_CC = "Type";

  private VlcRcStatusBuilder() {
    throw new IllegalStateException("Utility class to build VlcRcStatus objects.");
  }

  /**
   * Builds a VlcRcStatus object from the VlcRcStatus string response returned by
   * a vlc player.
   */
  public static VlcRcStatus build(String vlcRcStatusString) {
    if (vlcRcStatusString == null) {
      return null;
    }
    VlcRcStatus vlcRcStatus = new VlcRcStatus();
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode vlcStatusResponseJson = mapper.readTree(vlcRcStatusString);
      setVlcRcStatusRootMainAttributes(vlcStatusResponseJson, vlcRcStatus);
      setVlcRcStatusRootAdditionalAttributes(vlcStatusResponseJson, vlcRcStatus);
      setVlcRcStatusStats(vlcStatusResponseJson, vlcRcStatus);
      setVlcRcStatusAudioFilters(vlcStatusResponseJson, vlcRcStatus);
      setVlcRcStatusVideoEffects(vlcStatusResponseJson, vlcRcStatus);
      setVlcRcStatusEqualizer(vlcStatusResponseJson, vlcRcStatus);
      setVlcRcStatusInformation(vlcStatusResponseJson, vlcRcStatus);
    } catch (IOException e) {
      logger.error("Error parsing input VlcRcStatus", e);
      vlcRcStatus = null;
    }
    return vlcRcStatus;
  }

  /**
   * Sets VlcRcStatus root main attributes.
   */
  private static void setVlcRcStatusRootMainAttributes(JsonNode jsonNode, VlcRcStatus vlcRcStatus) {
    vlcRcStatus.setFullscreen(JsonUtils.getBoolean(jsonNode, "fullscreen"));
    vlcRcStatus.setRepeat(JsonUtils.getBoolean(jsonNode, "repeat"));
    vlcRcStatus.setAspectRatio(JsonUtils.getText(jsonNode, "aspectratio"));
    vlcRcStatus.setCurrentPlId(JsonUtils.getInt(jsonNode, "currentplid"));
    vlcRcStatus.setTime(JsonUtils.getInt(jsonNode, "time"));
    vlcRcStatus.setVolume(JsonUtils.getInt(jsonNode, "volume"));
    vlcRcStatus.setLength(JsonUtils.getInt(jsonNode, "length"));
    vlcRcStatus.setRandom(JsonUtils.getBoolean(jsonNode, "random"));
    vlcRcStatus.setState(JsonUtils.getText(jsonNode, "state"));
    vlcRcStatus.setLoop(JsonUtils.getBoolean(jsonNode, "loop"));
  }

  /**
   * Sets VlcRcStatus root additional attributes.
   */
  private static void setVlcRcStatusRootAdditionalAttributes(JsonNode jsonNode,
      VlcRcStatus vlcRcStatus) {
    vlcRcStatus.setApiVersion(JsonUtils.getInt(jsonNode, "apiversion"));
    vlcRcStatus.setAudioDelay(JsonUtils.getInt(jsonNode, "audiodelay"));
    vlcRcStatus.setPosition(JsonUtils.getDouble(jsonNode, "position"));
    vlcRcStatus.setRate(JsonUtils.getInt(jsonNode, "rate"));
    vlcRcStatus.setSubtitleDelay(JsonUtils.getInt(jsonNode, "subtitledelay"));
    vlcRcStatus.setVersion(JsonUtils.getText(jsonNode, "version"));
  }

  /**
   * Sets VlcRcStatus stats.
   */
  private static void setVlcRcStatusStats(JsonNode vlcStatusResponseJson, VlcRcStatus vlcRcStatus) {
    VlcRcStatus.Stats stats = new VlcRcStatus.Stats();
    JsonNode jsonNode = vlcStatusResponseJson.get("stats");
    if (jsonNode != null) {
      stats.setInputBitrate(JsonUtils.getDouble(jsonNode, "inputbitrate"));
      stats.setSentBytes(JsonUtils.getInt(jsonNode, "sentbytes"));
      stats.setLostaBuffers(JsonUtils.getInt(jsonNode, "lostabuffers"));
      stats.setAverageDemuxBitrate(JsonUtils.getDouble(jsonNode, "averagedemuxbitrate"));
      stats.setReadPackets(JsonUtils.getInt(jsonNode, "readpackets"));
      stats.setDemuxReadPackets(JsonUtils.getInt(jsonNode, "demuxreadpackets"));
      stats.setLostPictures(JsonUtils.getInt(jsonNode, "lostpictures"));
      stats.setDisplayedPictures(JsonUtils.getInt(jsonNode, "displayedpictures"));
      stats.setSentPackets(JsonUtils.getInt(jsonNode, "sentpackets"));
      stats.setDemuxReadBytes(JsonUtils.getInt(jsonNode, "demuxreadbytes"));
      stats.setDemuxBitrate(JsonUtils.getDouble(jsonNode, "demuxbitrate"));
      stats.setPlayedaBuffers(JsonUtils.getInt(jsonNode, "playedabuffers"));
      stats.setDemuxDiscontinuity(JsonUtils.getInt(jsonNode, "demuxdiscontinuity"));
      stats.setDecodedAudio(JsonUtils.getInt(jsonNode, "decodedaudio"));
      stats.setSendBitrate(JsonUtils.getDouble(jsonNode, "sendbitrate"));
      stats.setReadBytes(JsonUtils.getInt(jsonNode, "readbytes"));
      stats.setAverageInputBitrate(JsonUtils.getDouble(jsonNode, "averageinputbitrate"));
      stats.setDemuxCorrupted(JsonUtils.getInt(jsonNode, "demuxcorrupted"));
      stats.setDecodedVideo(JsonUtils.getInt(jsonNode, "decodedvideo"));
    }
    vlcRcStatus.setStats(stats);
  }

  /**
   * Sets VlcRcStatus audio filters.
   */
  private static void setVlcRcStatusAudioFilters(JsonNode vlcStatusResponseJson,
      VlcRcStatus vlcRcStatus) {
    Map<String, String> audioFilters = new HashMap<>();
    JsonNode audioFiltersJson = vlcStatusResponseJson.get("audiofilters");
    if (audioFiltersJson != null) {
      Iterator<Entry<String, JsonNode>> audioFiltersIterator = audioFiltersJson.fields();
      while (audioFiltersIterator.hasNext()) {
        Entry<String, JsonNode> audioFiltersEntry = audioFiltersIterator.next();
        audioFilters.put(audioFiltersEntry.getKey(), audioFiltersEntry.getValue().asText());
      }
      vlcRcStatus.setAudioFilters(audioFilters);
    }
  }

  /**
   * Sets VlcRcStatus video filters.
   */
  private static void setVlcRcStatusVideoEffects(JsonNode vlcStatusResponseJson,
      VlcRcStatus vlcRcStatus) {
    VlcRcStatus.VideoEffects videoEffects = new VlcRcStatus.VideoEffects();
    JsonNode jsonNode = vlcStatusResponseJson.get("videoeffects");
    if (jsonNode != null) {
      videoEffects.setBrightness(JsonUtils.getInt(jsonNode, "brightness"));
      videoEffects.setContrast(JsonUtils.getInt(jsonNode, "contrast"));
      videoEffects.setGamma(JsonUtils.getInt(jsonNode, "gamma"));
      videoEffects.setHue(JsonUtils.getInt(jsonNode, "hue"));
      videoEffects.setSaturation(JsonUtils.getInt(jsonNode, "saturation"));
      vlcRcStatus.setVideoEffects(videoEffects);
    }
  }

  /**
   * Sets VlcRcStatus equalizer.
   */
  private static void setVlcRcStatusEqualizer(JsonNode vlcStatusResponseJson,
      VlcRcStatus vlcRcStatus) {
    JsonNode equalizerJson = vlcStatusResponseJson.get("equalizer");
    if (equalizerJson != null) {
      VlcRcStatus.Equalizer equalizer = new VlcRcStatus.Equalizer();

      JsonNode presetsJson = equalizerJson.get("presets");
      if (presetsJson != null) {
        Iterator<Entry<String, JsonNode>> presetsIterator = presetsJson.fields();
        Map<String, String> equalizerPresets = new HashMap<>();
        while (presetsIterator.hasNext()) {
          Entry<String, JsonNode> presetsEntry = presetsIterator.next();
          equalizerPresets.put(presetsEntry.getKey(), presetsEntry.getValue().asText());
        }
        equalizer.setPresets(equalizerPresets);
      }

      JsonNode bandsJson = equalizerJson.get("bands");
      if (bandsJson != null) {
        Iterator<Entry<String, JsonNode>> bandsIterator = bandsJson.fields();
        Map<String, Integer> equalizerBands = new HashMap<>();
        while (bandsIterator.hasNext()) {
          Entry<String, JsonNode> bandsEntry = bandsIterator.next();
          equalizerBands.put(bandsEntry.getKey(), bandsEntry.getValue().asInt());
        }
        equalizer.setBands(equalizerBands);
      }
      equalizer.setPreAmp(JsonUtils.getInt(equalizerJson, "preamp"));
      vlcRcStatus.setEqualizer(equalizer);
    }
  }

  /**
   * Sets VlcRcStatus information.
   */
  private static void setVlcRcStatusInformation(JsonNode vlcStatusResponseJson,
      VlcRcStatus vlcRcStatus) {
    JsonNode informationJson = vlcStatusResponseJson.get("information");
    if (informationJson != null) {
      VlcRcStatus.Information information = new VlcRcStatus.Information();
      information.setChapter(JsonUtils.getText(informationJson, "chapter"));
      List<String> chapters = new ArrayList<>();
      String chaptersStr = JsonUtils.getText(informationJson, "chapters");
      if (chaptersStr != null) {
        String[] chaptersArray = chaptersStr.split(",");
        chapters.addAll(Arrays.asList(chaptersArray));
      }
      information.setChapters(chapters);
      information.setTitle(JsonUtils.getText(informationJson, TITLE));
      List<String> titles = new ArrayList<>();
      String titlesStr = JsonUtils.getText(informationJson, "titles");
      if (titlesStr != null) {
        String[] titlesArray = titlesStr.split(",");
        titles.addAll(Arrays.asList(titlesArray));
      }
      information.setTitles(titles);
      setInformationCategories(informationJson, information);
      vlcRcStatus.setInformation(information);
    }
  }

  /**
   * Sets vlcRcStatus information categories. Current ones are subclasses Audio,
   * Video, Subtitle and Meta.
   */
  private static void setInformationCategories(JsonNode informationJson,
      VlcRcStatus.Information information) {
    JsonNode categoryJson = informationJson.get("category");
    Iterator<Entry<String, JsonNode>> categoryIterator = categoryJson.fields();
    while (categoryIterator.hasNext()) {
      Entry<String, JsonNode> categoryEntry = categoryIterator.next();
      String name = categoryEntry.getKey();
      JsonNode categoryNode = categoryEntry.getValue();
      if ("meta".equals(name)) {
        setInformationMeta(categoryNode, name, information);
      } else {
        String type = JsonUtils.getText(categoryNode, TYPE_CC);
        switch (type) {
          case "Video":
            setInformationVideo(categoryNode, name, information);
            break;
          case "Audio":
            setInformationAudio(categoryNode, name, information);
            break;
          case "Subtitle":
            setInformationSubtitle(categoryNode, name, information);
            break;
          default:
            logger.warn("Unrecognized Information category Type returned by VLC: {}", type);
            break;
        }
      }
    }
  }

  /**
   * Sets vlcRcStatus information meta category.
   */
  private static void setInformationMeta(JsonNode jsonNode, String name,
      VlcRcStatus.Information information) {
    VlcRcStatus.Information.Meta meta = new VlcRcStatus.Information.Meta();
    meta.setArtist(JsonUtils.getText(jsonNode, ARTIST));
    meta.setFilename(JsonUtils.getText(jsonNode, FILENAME));
    meta.setName(name);
    meta.setSetting(JsonUtils.getText(jsonNode, SETTING));
    meta.setSoftware(JsonUtils.getText(jsonNode, "Software"));
    meta.setTitle(JsonUtils.getText(jsonNode, TITLE));
    meta.setArtworkUrl(JsonUtils.getText(jsonNode, "artwork_url"));
    information.setMeta(meta);
  }

  /**
   * Sets vlcRcStatus information video category.
   */
  private static void setInformationVideo(JsonNode jsonNode, String name,
      VlcRcStatus.Information information) {
    VlcRcStatus.Information.Video video = new VlcRcStatus.Information.Video();
    video.setCodec(JsonUtils.getText(jsonNode, CODEC_CC));
    video.setDecodedFormat(JsonUtils.getText(jsonNode, "Decoded_format"));
    video.setDisplayResolution(JsonUtils.getText(jsonNode, "Display_resolution"));
    video.setFrameRate(JsonUtils.getText(jsonNode, "Frame_rate"));
    video.setLanguage(JsonUtils.getText(jsonNode, LANGUAGE_CC));
    video.setName(name);
    video.setResolution(JsonUtils.getText(jsonNode, "Resolution"));
    video.setType(JsonUtils.getText(jsonNode, TYPE_CC));
    information.setVideo(video);
  }

  /**
   * Sets vlcRcStatus information audio category.
   */
  private static void setInformationAudio(JsonNode jsonNode, String name,
      VlcRcStatus.Information information) {
    VlcRcStatus.Information.Audio audio = new VlcRcStatus.Information.Audio();
    audio.setCodec(JsonUtils.getText(jsonNode, CODEC_CC));
    audio.setLanguage(JsonUtils.getText(jsonNode, LANGUAGE_CC));
    audio.setType(JsonUtils.getText(jsonNode, TYPE_CC));
    audio.setName(name);
    audio.setBitrate(JsonUtils.getText(jsonNode, "Bitrate"));
    audio.setChannels(JsonUtils.getText(jsonNode, "Channels"));
    audio.setSampleRate(JsonUtils.getText(jsonNode, "Sample_rate"));
    information.setAudio(audio);
  }

  /**
   * Sets vlcRcStatus information audio category.
   */
  private static void setInformationSubtitle(JsonNode jsonNode, String name,
      VlcRcStatus.Information information) {
    VlcRcStatus.Information.Subtitle subtitle = new VlcRcStatus.Information.Subtitle();
    subtitle.setCodec(JsonUtils.getText(jsonNode, CODEC_CC));
    subtitle.setLanguage(JsonUtils.getText(jsonNode, LANGUAGE_CC));
    subtitle.setType(JsonUtils.getText(jsonNode, TYPE_CC));
    subtitle.setName(name);
    information.setSubtitle(subtitle);
  }
}
