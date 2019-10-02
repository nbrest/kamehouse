package com.nicobrest.kamehouse.vlcrc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.main.dao.Identifiable;
import com.nicobrest.kamehouse.main.exception.KameHouseException;
import com.nicobrest.kamehouse.main.utils.HttpClientUtils;
import com.nicobrest.kamehouse.main.utils.JsonUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a VLC Player in the system. It connects to the web API of the VLC
 * Player that it represents to execute commands on it and retrieve the status
 * of the player.
 * 
 * @author nbrest
 *
 */
@Entity
@Table(name = "VLC_PLAYER")
public class VlcPlayer implements Identifiable, Serializable {

  @JsonIgnore
  private static final long serialVersionUID = 1L;
  @JsonIgnore
  private static final Logger logger = LoggerFactory.getLogger(VlcPlayer.class);
  @JsonIgnore
  private static final String PROTOCOL = "http://";
  @JsonIgnore
  private static final String STATUS_URL = "/requests/status.json";
  @JsonIgnore
  private static final String PLAYLIST_URL = "/requests/playlist.json";
  @JsonIgnore
  private static final String BROWSE_URL = "/requests/browse.json";
  @JsonIgnore
  private static final String FILENAME = "filename";
  @JsonIgnore
  private static final String ARTIST = "artist";
  @JsonIgnore
  private static final String SETTING = "setting";
  @JsonIgnore
  private static final String TITLE = "title";
  @JsonIgnore
  private static final String CODEC = "codec";
  @JsonIgnore
  private static final String CODEC_CC = "Codec";
  @JsonIgnore
  private static final String LANGUAGE = "language";
  @JsonIgnore
  private static final String LANGUAGE_CC = "Language";
  @JsonIgnore
  private static final String TYPE_CC = "Type";
  @Id
  @Column(name = "ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "HOSTNAME", unique = true, nullable = false)
  private String hostname;

  @Column(name = "PORT")
  private int port;

  @Column(name = "USERNAME")
  private String username;

  @Column(name = "PASSWORD")
  private String password;

  public VlcPlayer() {
    super();
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getHostname() {
    return hostname;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  /**
   * Execute a command in the VLC Player and return it's status.
   */
  public VlcRcStatus execute(VlcRcCommand command) {
    String commandUrl = buildCommandUrl(command);
    if (commandUrl != null) {
      String vlcServerResponse = executeRequestToVlcServer(commandUrl);
      return buildVlcRcStatus(vlcServerResponse);
    } else {
      return null;
    }
  }

  /**
   * Get the status information of the VLC Player.
   */
  @JsonIgnore
  public VlcRcStatus getVlcRcStatus() {
    StringBuilder statusUrl = new StringBuilder();
    statusUrl.append(PROTOCOL);
    statusUrl.append(hostname);
    statusUrl.append(":");
    statusUrl.append(port);
    statusUrl.append(STATUS_URL);
    VlcRcStatus vlcRcStatus = null;
    String vlcServerResponse = executeRequestToVlcServer(statusUrl.toString());
    vlcRcStatus = buildVlcRcStatus(vlcServerResponse);
    return vlcRcStatus;
  }

  /**
   * Gets the current playlist.
   */
  @JsonIgnore
  public List<VlcRcPlaylistItem> getPlaylist() {
    StringBuilder playlistUrl = new StringBuilder();
    playlistUrl.append(PROTOCOL);
    playlistUrl.append(hostname);
    playlistUrl.append(":");
    playlistUrl.append(port);
    playlistUrl.append(PLAYLIST_URL);
    String vlcServerResponse = executeRequestToVlcServer(playlistUrl.toString());
    return buildVlcRcPlaylist(vlcServerResponse);
  }

  /**
   * Browse through the server running vlc.
   */
  public List<VlcRcFileListItem> browse(String uri) {
    StringBuilder browseUrl = new StringBuilder();
    browseUrl.append(PROTOCOL);
    browseUrl.append(hostname);
    browseUrl.append(":");
    browseUrl.append(port);
    browseUrl.append(BROWSE_URL);
    if (uri != null) {
      browseUrl.append("?uri=" + HttpClientUtils.urlEncode(uri));
    } else {
      browseUrl.append("?uri=file:///");
    }
    String vlcServerResponse = executeRequestToVlcServer(browseUrl.toString());
    return buildVlcRcFilelist(vlcServerResponse);
  }

  /**
   * Builds the URL to execute the command in the VLC Player through its web
   * API.
   */
  private String buildCommandUrl(VlcRcCommand command) {
    String encodedCommand = HttpClientUtils.urlEncode(command.getName());
    if (encodedCommand == null) {
      return null;
    }
    StringBuilder commandUrl = new StringBuilder();
    commandUrl.append(PROTOCOL);
    commandUrl.append(hostname);
    commandUrl.append(":");
    commandUrl.append(port);
    commandUrl.append(STATUS_URL);
    commandUrl.append("?command=" + encodedCommand);
    if (command.getInput() != null) {
      commandUrl.append("&input=" + HttpClientUtils.urlEncode(command.getInput()));
    }
    if (command.getOption() != null) {
      commandUrl.append("&option=" + HttpClientUtils.urlEncode(command.getOption()));
    }
    if (command.getVal() != null) {
      commandUrl.append("&val=" + HttpClientUtils.urlEncode(command.getVal()));
    }
    if (command.getId() != null) {
      commandUrl.append("&id=" + HttpClientUtils.urlEncode(command.getId()));
    }
    if (command.getBand() != null) {
      commandUrl.append("&band=" + HttpClientUtils.urlEncode(command.getBand()));
    }
    return commandUrl.toString();
  }

  /**
   * Executes a request to the web API of the VLC Player using the provided URL
   * and returns the payload as a String.
   */
  @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING",
      justification = "Currently it's a limitation by using apache HttpClient. Created a task to "
          + "look at alternatives")
  private String executeRequestToVlcServer(String url) {
    HttpClient client = HttpClientUtils.getClient(username, password);
    HttpGet request = HttpClientUtils.httpGet(url);
    HttpResponse response;
    try {
      response = HttpClientUtils.executeRequest(client, request);
      try (InputStream resInStream = HttpClientUtils.getInputStreamFromResponse(response);
          BufferedReader responseReader = new BufferedReader(new InputStreamReader(resInStream))) {
        StringBuilder responseBody = new StringBuilder();
        String line = "";
        while ((line = responseReader.readLine()) != null) {
          responseBody.append(line);
        }
        return responseBody.toString();
      }
    } catch (IOException e) {
      logger.error("Error executing request. Message: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Converts the status information returned by the web API of the VLC Player
   * into its internal representation used in the application.
   */
  private VlcRcStatus buildVlcRcStatus(String vlcStatusResponseStr) {
    if (vlcStatusResponseStr == null) {
      return null;
    }
    VlcRcStatus vlcRcStatus = new VlcRcStatus();
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode vlcStatusResponseJson = mapper.readTree(vlcStatusResponseStr);
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
   * Set VlcRcStatus root main attributes.
   */
  private void setVlcRcStatusRootMainAttributes(JsonNode jsonNode, VlcRcStatus vlcRcStatus) {
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
   * Set VlcRcStatus root additional attributes.
   */
  private void setVlcRcStatusRootAdditionalAttributes(JsonNode jsonNode, VlcRcStatus vlcRcStatus) {
    vlcRcStatus.setApiVersion(JsonUtils.getInt(jsonNode, "apiversion"));
    vlcRcStatus.setAudioDelay(JsonUtils.getInt(jsonNode, "audiodelay"));
    vlcRcStatus.setPosition(JsonUtils.getDouble(jsonNode, "position"));
    vlcRcStatus.setRate(JsonUtils.getInt(jsonNode, "rate"));
    vlcRcStatus.setSubtitleDelay(JsonUtils.getInt(jsonNode, "subtitledelay"));
    vlcRcStatus.setVersion(JsonUtils.getText(jsonNode, "version"));
  }

  /**
   * Set VlcRcStatus stats.
   */
  private void setVlcRcStatusStats(JsonNode vlcStatusResponseJson, VlcRcStatus vlcRcStatus) {
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
   * Set VlcRcStatus audio filters.
   */
  private void setVlcRcStatusAudioFilters(JsonNode vlcStatusResponseJson,
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
   * Set VlcRcStatus video filters.
   */
  private void setVlcRcStatusVideoEffects(JsonNode vlcStatusResponseJson,
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
   * Set VlcRcStatus equalizer.
   */
  private void setVlcRcStatusEqualizer(JsonNode vlcStatusResponseJson, VlcRcStatus vlcRcStatus) {
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
   * Set VlcRcStatus information.
   */
  private void setVlcRcStatusInformation(JsonNode vlcStatusResponseJson, VlcRcStatus vlcRcStatus) {
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
   * Set vlcRcStatus information categories. Current ones are subclasses Audio, Video,
   * Subtitle and Meta.
   */
  private void setInformationCategories(JsonNode informationJson,
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
   * Set vlcRcStatus information meta category.
   */
  private void setInformationMeta(JsonNode jsonNode, String name, 
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
   * Set vlcRcStatus information video category.
   */
  private void setInformationVideo(JsonNode jsonNode, String name,
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
   * Set vlcRcStatus information audio category.
   */
  private void setInformationAudio(JsonNode jsonNode,String name,
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
   * Set vlcRcStatus information audio category.
   */
  private void setInformationSubtitle(JsonNode jsonNode,String name,
      VlcRcStatus.Information information) {
    VlcRcStatus.Information.Subtitle subtitle = new VlcRcStatus.Information.Subtitle();
    subtitle.setCodec(JsonUtils.getText(jsonNode, CODEC_CC));
    subtitle.setLanguage(JsonUtils.getText(jsonNode, LANGUAGE_CC));
    subtitle.setType(JsonUtils.getText(jsonNode, TYPE_CC));
    subtitle.setName(name);
    information.setSubtitle(subtitle);
  }

  /**
   * Converts the playlist returned by the VLC Player into an internal playlist
   * format.
   */
  private List<VlcRcPlaylistItem> buildVlcRcPlaylist(String vlcRcPlaylistResponse) {
    List<VlcRcPlaylistItem> vlcRcPlaylist = new ArrayList<>();
    if (vlcRcPlaylistResponse == null) {
      return vlcRcPlaylist;
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode vlcRcPlaylistResponseJson = mapper.readTree(vlcRcPlaylistResponse);
      JsonNode firstChildrenArray = vlcRcPlaylistResponseJson.get("children");
      if (!JsonUtils.isJsonNodeArrayEmpty(firstChildrenArray)) {
        for (JsonNode firstChildrenNode : firstChildrenArray) {
          if ("Playlist".equals(JsonUtils.getText(firstChildrenNode, "name"))) {
            JsonNode playlistArrayNode = firstChildrenNode.get("children");
            if (!JsonUtils.isJsonNodeArrayEmpty(playlistArrayNode)) {
              vlcRcPlaylist = getVlcRcPlaylistFromJsonNode(playlistArrayNode);
            }
          }
        }
      }
      return vlcRcPlaylist;
    } catch (IOException e) {
      throw new KameHouseException(e);
    }
  }

  /**
   * Iterate through the JsonNode array and generate the VlcRcPlaylist.
   */
  private List<VlcRcPlaylistItem> getVlcRcPlaylistFromJsonNode(JsonNode playlistArrayNode) {
    List<VlcRcPlaylistItem> vlcRcPlaylist = new ArrayList<>();
    for (JsonNode jsonNode : playlistArrayNode) {
      VlcRcPlaylistItem playlistItem = new VlcRcPlaylistItem();
      playlistItem.setId(JsonUtils.getInt(jsonNode, "id"));
      playlistItem.setName(JsonUtils.getText(jsonNode, "name"));
      playlistItem.setUri(JsonUtils.getText(jsonNode, "uri"));
      playlistItem.setDuration(JsonUtils.getInt(jsonNode, "duration"));
      vlcRcPlaylist.add(playlistItem);
    }
    return vlcRcPlaylist;
  }

  /**
   * Converts the file list returned by the VLC Player into an internal file
   * list format.
   */
  private List<VlcRcFileListItem> buildVlcRcFilelist(String vlcRcFileListResponse) {
    List<VlcRcFileListItem> vlcRcFilelist = new ArrayList<>();
    if (vlcRcFileListResponse == null) {
      return vlcRcFilelist;
    }
    String parsedVlcRcPlaylistResponse = vlcRcFileListResponse.replace("\\", "/");
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode vlcRcFileListResponseJson = mapper.readTree(parsedVlcRcPlaylistResponse);
      JsonNode elementArray = vlcRcFileListResponseJson.get("element");
      if (elementArray != null && elementArray.isArray()) {
        for (JsonNode jsonNode : elementArray) {
          VlcRcFileListItem fileListItem = new VlcRcFileListItem();
          fileListItem.setType(JsonUtils.getText(jsonNode, "type"));
          fileListItem.setName(JsonUtils.getText(jsonNode, "name"));
          fileListItem.setPath(JsonUtils.getText(jsonNode, "path"));
          fileListItem.setUri(JsonUtils.getText(jsonNode, "uri"));
          fileListItem.setSize(JsonUtils.getInt(jsonNode, "size"));
          fileListItem.setAccessTime(JsonUtils.getInt(jsonNode, "access_time"));
          fileListItem.setCreationTime(JsonUtils.getInt(jsonNode, "creation_time"));
          fileListItem.setModificationTime(JsonUtils.getInt(jsonNode, "modification_time"));
          fileListItem.setUid(JsonUtils.getInt(jsonNode, "uid"));
          fileListItem.setGid(JsonUtils.getInt(jsonNode, "gid"));
          fileListItem.setMode(JsonUtils.getInt(jsonNode, "mode"));
          vlcRcFilelist.add(fileListItem);
        }
      }
      return vlcRcFilelist;
    } catch (IOException e) {
      throw new KameHouseException(e);
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(hostname).append(port).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof VlcPlayer) {
      final VlcPlayer other = (VlcPlayer) obj;
      return new EqualsBuilder().append(id, other.getId()).append(hostname, other.getHostname())
          .append(port, other.getPort()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
