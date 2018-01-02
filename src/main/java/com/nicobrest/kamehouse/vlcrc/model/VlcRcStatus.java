package com.nicobrest.kamehouse.vlcrc.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the status returned by a VLC Player.
 * 
 * @author nbrest
 * 
 */
public class VlcRcStatus {

  private Boolean fullscreen;
  private Boolean repeat;
  private int subtitleDelay;
  private String aspectRatio;
  private int audioDelay;
  private int apiVersion;
  private int currentPlId;
  private int time;
  private int volume;
  private int length;
  private Boolean random;
  private int rate;
  private String state;
  private Boolean loop;
  private double position;
  private String version;
  private Equalizer equalizer;

  /*
   * audioFilters:
   * 
   * filterName : value
   */
  private Map<String, String> audioFilters;

  /*
   * stats:
   * 
   * inputBitrate; sentBytes; lostaBuffers; averageDemuxBitrate; readPackets;
   * demuxReadPackets; lostPictures;displayedPictures; sentPackets;
   * demuxReadBytes; demuxBitrate; playedaBuffers; demuxDiscontinuity;
   * decodedAudio; sendBitrate; readBytes; averageInputBitrate; demuxCorrupted;
   * decodedVideo;
   */
  private Map<String, Object> stats;

  /*
   * videoEffects:
   * 
   * hue; saturation; contrast; brightness; gamma
   */
  private Map<String, Integer> videoEffects;

  private Information information;

  public Boolean getFullscreen() {
    return fullscreen;
  }

  public void setFullscreen(Boolean fullscreen) {
    this.fullscreen = fullscreen;
  }

  public Boolean getRepeat() {
    return repeat;
  }

  public void setRepeat(Boolean repeat) {
    this.repeat = repeat;
  }

  public int getSubtitleDelay() {
    return subtitleDelay;
  }

  public void setSubtitleDelay(int subtitleDelay) {
    this.subtitleDelay = subtitleDelay;
  }

  public String getAspectRatio() {
    return aspectRatio;
  }

  public void setAspectRatio(String aspectRatio) {
    this.aspectRatio = aspectRatio;
  }

  public int getAudioDelay() {
    return audioDelay;
  }

  public void setAudioDelay(int audioDelay) {
    this.audioDelay = audioDelay;
  }

  public int getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(int apiVersion) {
    this.apiVersion = apiVersion;
  }

  public int getCurrentPlId() {
    return currentPlId;
  }

  public void setCurrentPlId(int currentPlId) {
    this.currentPlId = currentPlId;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public Boolean getRandom() {
    return random;
  }

  public void setRandom(Boolean random) {
    this.random = random;
  }

  public int getRate() {
    return rate;
  }

  public void setRate(int rate) {
    this.rate = rate;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Boolean getLoop() {
    return loop;
  }

  public void setLoop(Boolean loop) {
    this.loop = loop;
  }

  public double getPosition() {
    return position;
  }

  public void setPosition(double position) {
    this.position = position;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Equalizer getEqualizer() {
    return equalizer;
  }

  public void setEqualizer(Equalizer equalizer) {
    this.equalizer = equalizer;
  }

  public Map<String, String> getAudioFilters() {
    return audioFilters;
  }

  public void setAudioFilters(Map<String, String> audioFilters) {
    this.audioFilters = audioFilters;
  }

  public Map<String, Object> getStats() {
    return stats;
  }

  public void setStats(Map<String, Object> stats) {
    this.stats = stats;
  }

  public Map<String, Integer> getVideoEffects() {
    return videoEffects;
  }

  public void setVideoEffects(Map<String, Integer> videoEffects) {
    this.videoEffects = videoEffects;
  }

  public Information getInformation() {
    return information;
  }

  public void setInformation(Information information) {
    this.information = information;
  }

  public static class Equalizer {

    private Map<String, String> presets;
    private Map<String, Integer> bands;
    private int preAmp;

    public Map<String, String> getPresets() {
      return presets;
    }

    public void setPresets(Map<String, String> presets) {
      this.presets = presets;
    }

    public Map<String, Integer> getBands() {
      return bands;
    }

    public void setBands(Map<String, Integer> bands) {
      this.bands = bands;
    }

    public int getPreAmp() {
      return preAmp;
    }

    public void setPreAmp(int preAmp) {
      this.preAmp = preAmp;
    }
  }

  public static class Information {

    private String chapter;
    private List<String> chapters;
    private String title;
    private List<String> titles;
    private List<Map<String, Object>> category;

    public String getChapter() {
      return chapter;
    }

    public void setChapter(String chapter) {
      this.chapter = chapter;
    }

    public List<String> getChapters() {
      return chapters;
    }

    public void setChapters(List<String> chapters) {
      this.chapters = chapters;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public List<String> getTitles() {
      return titles;
    }

    public void setTitles(List<String> titles) {
      this.titles = titles;
    }

    public List<Map<String, Object>> getCategory() {
      return category;
    }

    public void setCategory(List<Map<String, Object>> category) {
      this.category = category;
    }
  }
}
