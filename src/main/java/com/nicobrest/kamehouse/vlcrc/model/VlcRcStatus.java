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
  // audioFilters: Don't come with fixed key names, so I keep it as a Map
  private Map<String, String> audioFilters;
  private Stats stats;
  private VideoEffects videoEffects;

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

  public Stats getStats() {
    return stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public VideoEffects getVideoEffects() {
    return videoEffects;
  }

  public void setVideoEffects(VideoEffects videoEffects) {
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

  /**
   * Video effects of the VlcRcStatus.
   */
  public static class VideoEffects {

    private int hue;
    private int saturation;
    private int contrast;
    private int brightness;
    private int gamma;

    public int getHue() {
      return hue;
    }

    public void setHue(int hue) {
      this.hue = hue;
    }

    public int getSaturation() {
      return saturation;
    }

    public void setSaturation(int saturation) {
      this.saturation = saturation;
    }

    public int getContrast() {
      return contrast;
    }

    public void setContrast(int contrast) {
      this.contrast = contrast;
    }

    public int getBrightness() {
      return brightness;
    }

    public void setBrightness(int brightness) {
      this.brightness = brightness;
    }

    public int getGamma() {
      return gamma;
    }

    public void setGamma(int gamma) {
      this.gamma = gamma;
    }
  }

  /**
   * Stats element of the VlcRcStatus.
   */
  public static class Stats {

    private Double inputBitrate;
    private int sentBytes;
    private int lostaBuffers;
    private Double averageDemuxBitrate;
    private int readPackets;
    private int demuxReadPackets;
    private int lostPictures;
    private int displayedPictures;
    private int sentPackets;
    private int demuxReadBytes;
    private Double demuxBitrate;
    private int playedaBuffers;
    private int demuxDiscontinuity;
    private int decodedAudio;
    private Double sendBitrate;
    private int readBytes;
    private Double averageInputBitrate;
    private int demuxCorrupted;
    private int decodedVideo;

    public Double getInputBitrate() {
      return inputBitrate;
    }

    public void setInputBitrate(Double inputBitrate) {
      this.inputBitrate = inputBitrate;
    }

    public int getSentBytes() {
      return sentBytes;
    }

    public void setSentBytes(int sentBytes) {
      this.sentBytes = sentBytes;
    }

    public int getLostaBuffers() {
      return lostaBuffers;
    }

    public void setLostaBuffers(int lostaBuffers) {
      this.lostaBuffers = lostaBuffers;
    }

    public Double getAverageDemuxBitrate() {
      return averageDemuxBitrate;
    }

    public void setAverageDemuxBitrate(Double averageDemuxBitrate) {
      this.averageDemuxBitrate = averageDemuxBitrate;
    }

    public int getReadPackets() {
      return readPackets;
    }

    public void setReadPackets(int readPackets) {
      this.readPackets = readPackets;
    }

    public int getDemuxReadPackets() {
      return demuxReadPackets;
    }

    public void setDemuxReadPackets(int demuxReadPackets) {
      this.demuxReadPackets = demuxReadPackets;
    }

    public int getLostPictures() {
      return lostPictures;
    }

    public void setLostPictures(int lostPictures) {
      this.lostPictures = lostPictures;
    }

    public int getDisplayedPictures() {
      return displayedPictures;
    }

    public void setDisplayedPictures(int displayedPictures) {
      this.displayedPictures = displayedPictures;
    }

    public int getSentPackets() {
      return sentPackets;
    }

    public void setSentPackets(int sentPackets) {
      this.sentPackets = sentPackets;
    }

    public int getDemuxReadBytes() {
      return demuxReadBytes;
    }

    public void setDemuxReadBytes(int demuxReadBytes) {
      this.demuxReadBytes = demuxReadBytes;
    }

    public Double getDemuxBitrate() {
      return demuxBitrate;
    }

    public void setDemuxBitrate(Double demuxBitrate) {
      this.demuxBitrate = demuxBitrate;
    }

    public int getPlayedaBuffers() {
      return playedaBuffers;
    }

    public void setPlayedaBuffers(int playedaBuffers) {
      this.playedaBuffers = playedaBuffers;
    }

    public int getDemuxDiscontinuity() {
      return demuxDiscontinuity;
    }

    public void setDemuxDiscontinuity(int demuxDiscontinuity) {
      this.demuxDiscontinuity = demuxDiscontinuity;
    }

    public int getDecodedAudio() {
      return decodedAudio;
    }

    public void setDecodedAudio(int decodedAudio) {
      this.decodedAudio = decodedAudio;
    }

    public Double getSendBitrate() {
      return sendBitrate;
    }

    public void setSendBitrate(Double sendBitrate) {
      this.sendBitrate = sendBitrate;
    }

    public int getReadBytes() {
      return readBytes;
    }

    public void setReadBytes(int readBytes) {
      this.readBytes = readBytes;
    }

    public Double getAverageInputBitrate() {
      return averageInputBitrate;
    }

    public void setAverageInputBitrate(Double averageInputBitrate) {
      this.averageInputBitrate = averageInputBitrate;
    }

    public int getDemuxCorrupted() {
      return demuxCorrupted;
    }

    public void setDemuxCorrupted(int demuxCorrupted) {
      this.demuxCorrupted = demuxCorrupted;
    }

    public int getDecodedVideo() {
      return decodedVideo;
    }

    public void setDecodedVideo(int decodedVideo) {
      this.decodedVideo = decodedVideo;
    }
  }

  /**
   * Information element of the VlcRcStatus.
   */
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
