package com.nicobrest.kamehouse.vlcrc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.main.exception.KameHouseException;
import com.nicobrest.kamehouse.utils.JsonUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
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
public class VlcPlayer implements Serializable {

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
  private static final String TITLE = "title";
  @JsonIgnore
  private static final String CODEC = "codec";
  @JsonIgnore
  private static final String CODEC_CAMEL_CASE = "Codec";
  @JsonIgnore
  private static final String LANGUAGE = "language";
  @JsonIgnore
  private static final String LANGUAGE_CAMEL_CASE = "Language";

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
  public List<Map<String, Object>> getPlaylist() {
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
  public List<Map<String, Object>> browse(String uri) {
    StringBuilder browseUrl = new StringBuilder();
    browseUrl.append(PROTOCOL);
    browseUrl.append(hostname);
    browseUrl.append(":");
    browseUrl.append(port);
    browseUrl.append(BROWSE_URL);
    if (uri != null) {
      browseUrl.append("?uri=" + urlEncode(uri));
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

    String encodedCommand = urlEncode(command.getName());
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
      commandUrl.append("&input=" + urlEncode(command.getInput()));
    }
    if (command.getOption() != null) {
      commandUrl.append("&option=" + urlEncode(command.getOption()));
    }
    if (command.getVal() != null) {
      commandUrl.append("&val=" + urlEncode(command.getVal()));
    }
    if (command.getId() != null) {
      commandUrl.append("&id=" + urlEncode(command.getId()));
    }
    if (command.getBand() != null) {
      commandUrl.append("&band=" + urlEncode(command.getBand()));
    }
    return commandUrl.toString();
  }

  private String urlEncode(String parameter) {
    try {
      return URIUtil.encodeQuery(parameter);
    } catch (URIException | IllegalArgumentException e) {
      logger.error("Failed to encode parameter: {}", parameter);
      return null;
    }
  }

  /**
   * Executes a request to the web API of the VLC Player using the provided URL
   * and returns the payload as a String.
   */
  @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING",
      justification = "Currently it's a limitation by using apache HttpClient. Created a task to "
          + "look at alternatives")
  private String executeRequestToVlcServer(String url) {
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
    credentialsProvider.setCredentials(AuthScope.ANY, credentials);
    HttpClient client = createHttpClient(credentialsProvider);
    HttpGet request = new HttpGet(url);
    HttpResponse response;
    try {
      response = executeGetRequest(client, request);
      try (InputStream inputStreamFromResponse = getInputStreamFromResponse(response);
          BufferedReader responseReader = new BufferedReader(new InputStreamReader(
              inputStreamFromResponse))) {
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
   * Creates an instance of HttpClient with the provided credentials.
   */
  private HttpClient createHttpClient(CredentialsProvider credentialsProvider) {
    return HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
  }

  /**
   * Execute the HTTP Get request to the specified HttpClient.
   */
  private HttpResponse executeGetRequest(HttpClient client, HttpGet getRequest)
      throws IOException {
    return client.execute(getRequest);
  }

  /**
   * Returns the response content as an InputStream.
   */
  private InputStream getInputStreamFromResponse(HttpResponse response) throws IOException {
    return response.getEntity().getContent();
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
      setVlcRcStatusRootAttributes(vlcStatusResponseJson, vlcRcStatus);
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
   * Set VlcRcStatus root attributes.
   */
  private void setVlcRcStatusRootAttributes(JsonNode vlcStatusResponseJson,
      VlcRcStatus vlcRcStatus) {
    /* Set root attributes */
    if (vlcStatusResponseJson.get("fullscreen") != null) {
      vlcRcStatus.setFullscreen(vlcStatusResponseJson.get("fullscreen").asBoolean());
    }
    if (vlcStatusResponseJson.get("repeat") != null) {
      vlcRcStatus.setRepeat(vlcStatusResponseJson.get("repeat").asBoolean());
    }
    if (vlcStatusResponseJson.get("subtitledelay") != null) {
      vlcRcStatus.setSubtitleDelay(vlcStatusResponseJson.get("subtitledelay").asInt());
    }
    if (vlcStatusResponseJson.get("aspectratio") != null) {
      vlcRcStatus.setAspectRatio(vlcStatusResponseJson.get("aspectratio").asText());
    }
    if (vlcStatusResponseJson.get("audiodelay") != null) {
      vlcRcStatus.setAudioDelay(vlcStatusResponseJson.get("audiodelay").asInt());
    }
    if (vlcStatusResponseJson.get("apiversion") != null) {
      vlcRcStatus.setApiVersion(vlcStatusResponseJson.get("apiversion").asInt());
    }
    if (vlcStatusResponseJson.get("currentplid") != null) {
      vlcRcStatus.setCurrentPlId(vlcStatusResponseJson.get("currentplid").asInt());
    }
    if (vlcStatusResponseJson.get("time") != null) {
      vlcRcStatus.setTime(vlcStatusResponseJson.get("time").asInt());
    }
    if (vlcStatusResponseJson.get("volume") != null) {
      vlcRcStatus.setVolume(vlcStatusResponseJson.get("volume").asInt());
    }
    if (vlcStatusResponseJson.get("length") != null) {
      vlcRcStatus.setLength(vlcStatusResponseJson.get("length").asInt());
    }
    if (vlcStatusResponseJson.get("random") != null) {
      vlcRcStatus.setRandom(vlcStatusResponseJson.get("random").asBoolean());
    }
    if (vlcStatusResponseJson.get("rate") != null) {
      vlcRcStatus.setRate(vlcStatusResponseJson.get("rate").asInt());
    }
    if (vlcStatusResponseJson.get("state") != null) {
      vlcRcStatus.setState(vlcStatusResponseJson.get("state").asText());
    }
    if (vlcStatusResponseJson.get("loop") != null) {
      vlcRcStatus.setLoop(vlcStatusResponseJson.get("loop").asBoolean());
    }
    if (vlcStatusResponseJson.get("position") != null) {
      vlcRcStatus.setPosition(vlcStatusResponseJson.get("position").asInt());
    }
    if (vlcStatusResponseJson.get("version") != null) {
      vlcRcStatus.setVersion(vlcStatusResponseJson.get("version").asText());
    }
  }

  /**
   * Set VlcRcStatus stats.
   */
  private void setVlcRcStatusStats(JsonNode vlcStatusResponseJson, VlcRcStatus vlcRcStatus) {
    /* Set stats */
    JsonNode statsJson = vlcStatusResponseJson.get("stats");
    Map<String, Object> stats = new HashMap<>();
    if (statsJson != null) {
      stats.put("inputBitrate", statsJson.get("inputbitrate"));
      stats.put("sentBytes", statsJson.get("sentbytes"));
      stats.put("lostaBuffers", statsJson.get("lostabuffers"));
      stats.put("averageDemuxBitrate", statsJson.get("averagedemuxbitrate"));
      stats.put("readPackets", statsJson.get("readpackets"));
      stats.put("demuxReadPackets", statsJson.get("demuxreadpackets"));
      stats.put("lostPictures", statsJson.get("lostpictures"));
      stats.put("displayedPictures", statsJson.get("displayedpictures"));
      stats.put("sentPackets", statsJson.get("sentpackets"));
      stats.put("demuxReadBytes", statsJson.get("demuxreadbytes"));
      stats.put("demuxBitrate", statsJson.get("demuxbitrate"));
      stats.put("playedaBuffers", statsJson.get("playedabuffers"));
      stats.put("demuxDiscontinuity", statsJson.get("demuxdiscontinuity"));
      stats.put("decodedAudio", statsJson.get("decodedaudio"));
      stats.put("sendBitrate", statsJson.get("sendbitrate"));
      stats.put("readBytes", statsJson.get("readbytes"));
      stats.put("averageInputBitrate", statsJson.get("averageinputbitrate"));
      stats.put("demuxCorrupted", statsJson.get("demuxcorrupted"));
      stats.put("decodedVideo", statsJson.get("decodedvideo"));
    }
    vlcRcStatus.setStats(stats);
  }

  /**
   * Set VlcRcStatus audio filters.
   */
  private void setVlcRcStatusAudioFilters(JsonNode vlcStatusResponseJson,
      VlcRcStatus vlcRcStatus) {
    /* Set audioFilters */
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
    /* Set videoEffects */
    Map<String, Integer> videoEffects = new HashMap<>();
    JsonNode videoEffectsJson = vlcStatusResponseJson.get("videoeffects");
    if (videoEffectsJson != null) {
      Iterator<Entry<String, JsonNode>> videoEffectsIterator = videoEffectsJson.fields();
      while (videoEffectsIterator.hasNext()) {
        Entry<String, JsonNode> videoEffectsEntry = videoEffectsIterator.next();
        videoEffects.put(videoEffectsEntry.getKey(), videoEffectsEntry.getValue().asInt());
      }
      vlcRcStatus.setVideoEffects(videoEffects);
    }
  }

  /**
   * Set VlcRcStatus equalizer.
   */
  private void setVlcRcStatusEqualizer(JsonNode vlcStatusResponseJson, VlcRcStatus vlcRcStatus) {
    /* Set equalizer */
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

      JsonNode preAmpNode = equalizerJson.get("preamp");
      if (preAmpNode != null) {
        equalizer.setPreAmp(preAmpNode.asInt());
      }
      vlcRcStatus.setEqualizer(equalizer);
    }
  }

  /**
   * Set VlcRcStatus information.
   */
  private void setVlcRcStatusInformation(JsonNode vlcStatusResponseJson, VlcRcStatus vlcRcStatus) {
    /* Set information */
    JsonNode informationJson = vlcStatusResponseJson.get("information");
    if (informationJson != null) {
      VlcRcStatus.Information information = new VlcRcStatus.Information();
      information.setChapter(informationJson.get("chapter").asText());
      List<String> chapters = new ArrayList<>();
      String[] chaptersArray = informationJson.get("chapters").asText().split(",");
      chapters.addAll(Arrays.asList(chaptersArray));
      information.setChapters(chapters);

      information.setTitle(informationJson.get(TITLE).asText());
      List<String> titles = new ArrayList<>();
      String[] titlesArray = informationJson.get("titles").asText().split(",");
      titles.addAll(Arrays.asList(titlesArray));
      information.setTitles(titles);

      JsonNode categoryJson = informationJson.get("category");
      Iterator<Entry<String, JsonNode>> categoryIterator = categoryJson.fields();
      List<Map<String, Object>> informationCategories = new ArrayList<>();
      while (categoryIterator.hasNext()) {
        Map<String, Object> informationCategory = new HashMap<>();
        Entry<String, JsonNode> categoryEntry = categoryIterator.next();
        String name = categoryEntry.getKey();
        informationCategory.put("name", name);
        if (name.equals("meta")) {
          informationCategory.put("filename", categoryEntry.getValue().get("filename"));
          informationCategory.put(TITLE, categoryEntry.getValue().get(TITLE));
          informationCategory.put("artist", categoryEntry.getValue().get("artist"));
          informationCategory.put("setting", categoryEntry.getValue().get("setting"));
          informationCategory.put("software", categoryEntry.getValue().get("Software"));
        } else {
          String type = categoryEntry.getValue().get("Type").asText();
          informationCategory.put("type", type);
          switch (type) {
            case "Video":
              informationCategory.put("frameRate", categoryEntry.getValue().get("Frame_rate"));
              informationCategory.put("decodedFormat", categoryEntry.getValue().get(
                  "Decoded_format"));
              informationCategory.put("displayResolution", categoryEntry.getValue().get(
                  "Display_resolution"));
              informationCategory.put(CODEC, categoryEntry.getValue().get(CODEC_CAMEL_CASE));
              informationCategory.put(LANGUAGE, categoryEntry.getValue().get(LANGUAGE_CAMEL_CASE));
              informationCategory.put("resolution", categoryEntry.getValue().get("Resolution"));
              break;
            case "Audio":
              informationCategory.put("bitrate", categoryEntry.getValue().get("Bitrate"));
              informationCategory.put("channels", categoryEntry.getValue().get("Channels"));
              informationCategory.put("sampleRate", categoryEntry.getValue().get("Sample_rate"));
              informationCategory.put(CODEC, categoryEntry.getValue().get(CODEC_CAMEL_CASE));
              informationCategory.put(LANGUAGE, categoryEntry.getValue().get(LANGUAGE_CAMEL_CASE));
              break;
            case "Subtitle":
              informationCategory.put(CODEC, categoryEntry.getValue().get(CODEC_CAMEL_CASE));
              informationCategory.put(LANGUAGE, categoryEntry.getValue().get(LANGUAGE_CAMEL_CASE));
              break;
            default:
              logger.warn("Unrecognized Type returned by VLC: {}", type);
              break;
          }
        }
        informationCategories.add(informationCategory);
      }
      information.setCategory(informationCategories);
      vlcRcStatus.setInformation(information);
    }
  }

  /**
   * Converts the playlist returned by the VLC Player into an internal playlist
   * format.
   */
  private List<Map<String, Object>> buildVlcRcPlaylist(String vlcRcPlaylistResponse) {
    List<Map<String, Object>> vlcRcPlaylist = new ArrayList<>();
    if (vlcRcPlaylistResponse == null) {
      return vlcRcPlaylist;
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode vlcRcPlaylistResponseJson = mapper.readTree(vlcRcPlaylistResponse);
      JsonNode firstChildrenArray = vlcRcPlaylistResponseJson.get("children");
      if (!isJsonNodeArrayEmpty(firstChildrenArray)) {
        for (JsonNode firstChildrenNode : firstChildrenArray) {
          if ("Playlist".equals(firstChildrenNode.get("name").asText())) {
            JsonNode playlistArrayNode = firstChildrenNode.get("children");
            if (!isJsonNodeArrayEmpty(playlistArrayNode)) {
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
   * Checks if the specified JsonNode is an array and is not empty.
   */
  private boolean isJsonNodeArrayEmpty(JsonNode jsonNodeArray) {
    return !(jsonNodeArray != null && jsonNodeArray.isArray() && jsonNodeArray.size() > 0);
  }

  /**
   * Iterate through the JsonNode array and generate the VlcRcPlaylist.
   */
  private List<Map<String, Object>> getVlcRcPlaylistFromJsonNode(JsonNode playlistArrayNode) {
    List<Map<String, Object>> vlcRcPlaylist = new ArrayList<>();
    for (JsonNode playlistItemNode : playlistArrayNode) {
      Map<String, Object> playlistItem = new HashMap<>();
      playlistItem.put("id", playlistItemNode.get("id").asInt());
      playlistItem.put("name", playlistItemNode.get("name").asText());
      playlistItem.put("uri", playlistItemNode.get("uri").asText());
      playlistItem.put("duration", playlistItemNode.get("duration").asInt());
      vlcRcPlaylist.add(playlistItem);
    }
    return vlcRcPlaylist;
  }

  /**
   * Converts the file list returned by the VLC Player into an internal file
   * list format.
   */
  private List<Map<String, Object>> buildVlcRcFilelist(String vlcRcFileListResponse) {
    List<Map<String, Object>> vlcRcFilelist = new ArrayList<>();
    if (vlcRcFileListResponse == null) {
      return vlcRcFilelist;
    }
    String parsedVlcRcPlaylistResponse = vlcRcFileListResponse.replace("\\", "/");
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode vlcRcFileListResponseJson = mapper.readTree(parsedVlcRcPlaylistResponse);
      JsonNode elementArray = vlcRcFileListResponseJson.get("element");
      if (elementArray != null && elementArray.isArray()) {
        for (JsonNode fileListItemNode : elementArray) {
          Map<String, Object> fileListItem = new HashMap<>();
          fileListItem.put("type", fileListItemNode.get("type").asText());
          fileListItem.put("name", fileListItemNode.get("name").asText());
          fileListItem.put("path", fileListItemNode.get("path").asText());
          fileListItem.put("uri", fileListItemNode.get("uri").asText());
          fileListItem.put("size", fileListItemNode.get("size").asInt());
          fileListItem.put("accessTime", fileListItemNode.get("access_time").asInt());
          fileListItem.put("creationTime", fileListItemNode.get("creation_time").asInt());
          fileListItem.put("modificationTime", fileListItemNode.get("modification_time").asInt());
          fileListItem.put("uid", fileListItemNode.get("uid").asInt());
          fileListItem.put("gid", fileListItemNode.get("gid").asInt());
          fileListItem.put("mode", fileListItemNode.get("mode").asInt());
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
