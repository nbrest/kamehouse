package com.nicobrest.kamehouse.ztvrc.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder class to generate a ZtvRcStatus object based on an input. The ZtvRcStatus class is so
 * complex to build, that it seems right to separate it's creation to a helper class and not have it
 * mixed with the ZTV Player logic. It could even be a service.
 *
 * @author nbrest
 */
public class ZtvRcStatusBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ZtvRcStatusBuilder.class);
  private static final String FILENAME = "filename";
  private static final String ARTIST = "artist";
  private static final String SETTING = "setting";
  private static final String TITLE = "title";
  private static final String CODEC_CC = "Codec";
  private static final String LANGUAGE_CC = "Language";
  private static final String DESCRIPTION_CC = "Description";
  private static final String TYPE_CC = "Type";

  private ZtvRcStatusBuilder() {
    throw new IllegalStateException("Utility class to build ZtvRcStatus objects.");
  }

  /**
   * Builds a ZtvRcStatus object from the ZtvRcStatus string response returned by a ZTV Player.
   */
  public static ZtvRcStatus build(String ztvRcStatusString) {
    ZtvRcStatus ztvRcStatus = new ZtvRcStatus();
    if (StringUtils.isEmpty(ztvRcStatusString)) {
      return ztvRcStatus;
    }
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode ztvStatusResponseJson = mapper.readTree(ztvRcStatusString);
      setZtvRcStatusRootMainAttributes(ztvStatusResponseJson, ztvRcStatus);
      setZtvRcStatusRootAdditionalAttributes(ztvStatusResponseJson, ztvRcStatus);
      setZtvRcStatusStats(ztvStatusResponseJson, ztvRcStatus);
      setZtvRcStatusAudioFilters(ztvStatusResponseJson, ztvRcStatus);
      setZtvRcStatusVideoEffects(ztvStatusResponseJson, ztvRcStatus);
      setZtvRcStatusEqualizer(ztvStatusResponseJson, ztvRcStatus);
      setZtvRcStatusInformation(ztvStatusResponseJson, ztvRcStatus);
    } catch (IOException e) {
      LOGGER.error("Error parsing input ZtvRcStatus. Message: {}", e.getMessage());
    }
    return ztvRcStatus;
  }

  /**
   * Sets ZtvRcStatus root main attributes.
   */
  private static void setZtvRcStatusRootMainAttributes(JsonNode jsonNode, ZtvRcStatus ztvRcStatus) {
    ztvRcStatus.setFullscreen(JsonUtils.getBoolean(jsonNode, "fullscreen"));
    ztvRcStatus.setRepeat(JsonUtils.getBoolean(jsonNode, "repeat"));
    ztvRcStatus.setAspectRatio(JsonUtils.getText(jsonNode, "aspectratio"));
    ztvRcStatus.setCurrentPlId(JsonUtils.getInt(jsonNode, "currentplid"));
    ztvRcStatus.setTime(JsonUtils.getInt(jsonNode, "time"));
    ztvRcStatus.setVolume(JsonUtils.getInt(jsonNode, "volume"));
    ztvRcStatus.setLength(JsonUtils.getInt(jsonNode, "length"));
    ztvRcStatus.setRandom(JsonUtils.getBoolean(jsonNode, "random"));
    ztvRcStatus.setState(JsonUtils.getText(jsonNode, "state"));
    ztvRcStatus.setLoop(JsonUtils.getBoolean(jsonNode, "loop"));
  }

  /**
   * Sets ZtvRcStatus root additional attributes.
   */
  private static void setZtvRcStatusRootAdditionalAttributes(
      JsonNode jsonNode, ZtvRcStatus ztvRcStatus) {
    ztvRcStatus.setApiVersion(JsonUtils.getInt(jsonNode, "apiversion"));
    ztvRcStatus.setAudioDelay(JsonUtils.getInt(jsonNode, "audiodelay"));
    ztvRcStatus.setPosition(JsonUtils.getDouble(jsonNode, "position"));
    ztvRcStatus.setRate(JsonUtils.getInt(jsonNode, "rate"));
    ztvRcStatus.setSubtitleDelay(JsonUtils.getInt(jsonNode, "subtitledelay"));
    ztvRcStatus.setVersion(JsonUtils.getText(jsonNode, "version"));
  }

  /**
   * Sets ZtvRcStatus stats.
   */
  private static void setZtvRcStatusStats(JsonNode ztvStatusResponseJson, ZtvRcStatus ztvRcStatus) {
    ZtvRcStatus.Stats stats = new ZtvRcStatus.Stats();
    JsonNode jsonNode = ztvStatusResponseJson.get("stats");
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
    ztvRcStatus.setStats(stats);
  }

  /**
   * Sets ZtvRcStatus audio filters.
   */
  private static void setZtvRcStatusAudioFilters(
      JsonNode ztvStatusResponseJson, ZtvRcStatus ztvRcStatus) {
    Map<String, String> audioFilters = new HashMap<>();
    JsonNode audioFiltersJson = ztvStatusResponseJson.get("audiofilters");
    if (audioFiltersJson != null) {
      Iterator<Entry<String, JsonNode>> audioFiltersIterator = audioFiltersJson.fields();
      while (audioFiltersIterator.hasNext()) {
        Entry<String, JsonNode> audioFiltersEntry = audioFiltersIterator.next();
        audioFilters.put(audioFiltersEntry.getKey(), audioFiltersEntry.getValue().asText());
      }
      ztvRcStatus.setAudioFilters(audioFilters);
    }
  }

  /**
   * Sets ZtvRcStatus video filters.
   */
  private static void setZtvRcStatusVideoEffects(
      JsonNode ztvStatusResponseJson, ZtvRcStatus ztvRcStatus) {
    ZtvRcStatus.VideoEffects videoEffects = new ZtvRcStatus.VideoEffects();
    JsonNode jsonNode = ztvStatusResponseJson.get("videoeffects");
    if (jsonNode != null) {
      videoEffects.setBrightness(JsonUtils.getInt(jsonNode, "brightness"));
      videoEffects.setContrast(JsonUtils.getInt(jsonNode, "contrast"));
      videoEffects.setGamma(JsonUtils.getInt(jsonNode, "gamma"));
      videoEffects.setHue(JsonUtils.getInt(jsonNode, "hue"));
      videoEffects.setSaturation(JsonUtils.getInt(jsonNode, "saturation"));
      ztvRcStatus.setVideoEffects(videoEffects);
    }
  }

  /**
   * Sets ZtvRcStatus equalizer.
   */
  private static void setZtvRcStatusEqualizer(
      JsonNode ztvStatusResponseJson, ZtvRcStatus ztvRcStatus) {
    JsonNode equalizerJson = ztvStatusResponseJson.get("equalizer");
    if (equalizerJson != null) {
      ZtvRcStatus.Equalizer equalizer = new ZtvRcStatus.Equalizer();

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
      ztvRcStatus.setEqualizer(equalizer);
    }
  }

  /**
   * Sets ZtvRcStatus information.
   */
  private static void setZtvRcStatusInformation(
      JsonNode ztvStatusResponseJson, ZtvRcStatus ztvRcStatus) {
    JsonNode informationJson = ztvStatusResponseJson.get("information");
    if (informationJson != null) {
      ZtvRcStatus.Information information = new ZtvRcStatus.Information();
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
      ztvRcStatus.setInformation(information);
    }
  }

  /**
   * Sets ztvRcStatus information categories. Current ones are subclasses Audio, Video, Subtitle and
   * Meta.
   */
  private static void setInformationCategories(
      JsonNode informationJson, ZtvRcStatus.Information information) {
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
            addInformationSubtitle(categoryNode, name, information);
            break;
          default:
            LOGGER.warn("Unrecognized Information category Type returned by ZTV Player: {}", type);
            break;
        }
      }
    }
  }

  /**
   * Sets ztvRcStatus information meta category.
   */
  private static void setInformationMeta(
      JsonNode jsonNode, String name, ZtvRcStatus.Information information) {
    ZtvRcStatus.Information.Meta meta = new ZtvRcStatus.Information.Meta();
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
   * Sets ztvRcStatus information video category.
   */
  private static void setInformationVideo(
      JsonNode jsonNode, String name, ZtvRcStatus.Information information) {
    ZtvRcStatus.Information.Video video = new ZtvRcStatus.Information.Video();
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
   * Sets ztvRcStatus information audio category.
   */
  private static void setInformationAudio(
      JsonNode jsonNode, String name, ZtvRcStatus.Information information) {
    ZtvRcStatus.Information.Audio audio = new ZtvRcStatus.Information.Audio();
    audio.setCodec(JsonUtils.getText(jsonNode, CODEC_CC));
    audio.setLanguage(JsonUtils.getText(jsonNode, LANGUAGE_CC));
    audio.setType(JsonUtils.getText(jsonNode, TYPE_CC));
    audio.setName(name);
    audio.setNumber(name.replaceAll("\\D", ""));
    audio.setBitrate(JsonUtils.getText(jsonNode, "Bitrate"));
    audio.setChannels(JsonUtils.getText(jsonNode, "Channels"));
    audio.setSampleRate(JsonUtils.getText(jsonNode, "Sample_rate"));
    information.addAudio(audio);
  }

  /**
   * Sets ztvRcStatus information subtitle category.
   */
  private static void addInformationSubtitle(
      JsonNode jsonNode, String name, ZtvRcStatus.Information information) {
    ZtvRcStatus.Information.Subtitle subtitle = new ZtvRcStatus.Information.Subtitle();
    subtitle.setCodec(JsonUtils.getText(jsonNode, CODEC_CC));
    subtitle.setLanguage(JsonUtils.getText(jsonNode, LANGUAGE_CC));
    subtitle.setType(JsonUtils.getText(jsonNode, TYPE_CC));
    subtitle.setDescription(JsonUtils.getText(jsonNode, DESCRIPTION_CC));
    subtitle.setName(name);
    subtitle.setNumber(name.replaceAll("\\D", ""));
    information.addSubtitle(subtitle);
  }
}
