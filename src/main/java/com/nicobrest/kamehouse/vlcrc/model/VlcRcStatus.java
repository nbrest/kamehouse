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

  /**
   * Equalizer element of the VlcRcStatus.
   */
  public static class Equalizer {

    // presets: Don't come with fixed key names, so I keep it as a Map
    private Map<String, String> presets;
    // bands: Don't come with fixed key names, so I keep it as a Map
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
    private Audio audio;
    private Meta meta;
    private Subtitle subtitle;
    private Video video;

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

    public Audio getAudio() {
      return audio;
    }

    public void setAudio(Audio audio) {
      this.audio = audio;
    }

    public Meta getMeta() {
      return meta;
    }

    public void setMeta(Meta meta) {
      this.meta = meta;
    }

    public Subtitle getSubtitle() {
      return subtitle;
    }

    public void setSubtitle(Subtitle subtitle) {
      this.subtitle = subtitle;
    }

    public Video getVideo() {
      return video;
    }

    public void setVideo(Video video) {
      this.video = video;
    }

    /**
     * Audio element of the Information class of VlcRcStatus.
     */
    public static class Audio {
      private String name;
      private String type;
      private String bitrate;
      private String channels;
      private String sampleRate;
      private String codec;
      private String language;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public String getBitrate() {
        return bitrate;
      }

      public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
      }

      public String getChannels() {
        return channels;
      }

      public void setChannels(String channels) {
        this.channels = channels;
      }

      public String getSampleRate() {
        return sampleRate;
      }

      public void setSampleRate(String sampleRate) {
        this.sampleRate = sampleRate;
      }

      public String getCodec() {
        return codec;
      }

      public void setCodec(String codec) {
        this.codec = codec;
      }

      public String getLanguage() {
        return language;
      }

      public void setLanguage(String language) {
        this.language = language;
      }
    }

    /**
     * Meta element of the Information class of VlcRcStatus.
     */
    public static class Meta {
      private String name;
      private String filename;
      private String title;
      private String artist;
      private String setting;
      private String software;
      private String artworkUrl;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getFilename() {
        return filename;
      }

      public void setFilename(String filename) {
        this.filename = filename;
      }

      public String getTitle() {
        return title;
      }

      public void setTitle(String title) {
        this.title = title;
      }

      public String getArtist() {
        return artist;
      }

      public void setArtist(String artist) {
        this.artist = artist;
      }

      public String getSetting() {
        return setting;
      }

      public void setSetting(String setting) {
        this.setting = setting;
      }

      public String getSoftware() {
        return software;
      }

      public void setSoftware(String software) {
        this.software = software;
      }

      public String getArtworkUrl() {
        return artworkUrl;
      }

      public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
      }
    }

    /**
     * Subtitle element of the Information class of VlcRcStatus.
     */
    public static class Subtitle {
      private String name;
      private String type;
      private String codec;
      private String language;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public String getCodec() {
        return codec;
      }

      public void setCodec(String codec) {
        this.codec = codec;
      }

      public String getLanguage() {
        return language;
      }

      public void setLanguage(String language) {
        this.language = language;
      }
    }

    /**
     * Video element of the Information class of VlcRcStatus.
     */
    public static class Video {
      private String name;
      private String type;
      private String frameRate;
      private String decodedFormat;
      private String displayResolution;
      private String codec;
      private String resolution;
      private String language;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public String getFrameRate() {
        return frameRate;
      }

      public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
      }

      public String getDecodedFormat() {
        return decodedFormat;
      }

      public void setDecodedFormat(String decodedFormat) {
        this.decodedFormat = decodedFormat;
      }

      public String getDisplayResolution() {
        return displayResolution;
      }

      public void setDisplayResolution(String displayResolution) {
        this.displayResolution = displayResolution;
      }

      public String getCodec() {
        return codec;
      }

      public void setCodec(String codec) {
        this.codec = codec;
      }

      public String getResolution() {
        return resolution;
      }

      public void setResolution(String resolution) {
        this.resolution = resolution;
      }

      public String getLanguage() {
        return language;
      }

      public void setLanguage(String language) {
        this.language = language;
      }
    }
  }
}
