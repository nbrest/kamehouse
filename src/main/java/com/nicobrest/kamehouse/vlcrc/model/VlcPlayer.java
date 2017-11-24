package com.nicobrest.kamehouse.vlcrc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.exception.KameHouseException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a VLC Player in the system. It connects to the web API of the VLC
 * Player that it represents to execute commands on it and retrieve the status
 * of the player.
 * 
 * @author nbrest
 *
 */
public class VlcPlayer {

  @JsonIgnore
  private static final Logger logger = LoggerFactory.getLogger(VlcPlayer.class);
  @JsonIgnore
  private static final String PROTOCOL = "http://";
  @JsonIgnore
  private static final String BASE_URL = "/requests/status.json";

  private String hostname;
  private int port;
  private String username;
  private String password;

  public VlcPlayer() {
    super();
  }

  public VlcPlayer(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
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
    VlcRcStatus vlcRcStatus = executeRequestToVlcServer(commandUrl);
    return vlcRcStatus;
  }

  /**
   * Get the status information of the VLC Player.
   */
  @JsonIgnore
  public VlcRcStatus getVlcRcStatus() {
    StringBuffer statusUrl = new StringBuffer();
    statusUrl.append(PROTOCOL);
    statusUrl.append(hostname);
    statusUrl.append(":");
    statusUrl.append(port);
    statusUrl.append(BASE_URL);
    VlcRcStatus vlcRcStatus = executeRequestToVlcServer(statusUrl.toString());
    return vlcRcStatus;
  }

  /**
   * Builds the URL to execute the command in the VLC Player through its web
   * API.
   */
  private String buildCommandUrl(VlcRcCommand command) {

    StringBuffer commandUrl = new StringBuffer();
    commandUrl.append(PROTOCOL);
    commandUrl.append(hostname);
    commandUrl.append(":");
    commandUrl.append(port);
    commandUrl.append(BASE_URL);
    commandUrl.append("?command=" + urlEncode(command.getName()));
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
    } catch (URIException e) {
      logger.error("Failed to encode parameter: " + parameter);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Executes a request to the web API of the VLC Player using the provided URL.
   */
  @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING",
      justification = "Currently it's a limitation by using apache HttpClient. Created a task to "
          + "look at alternatives")
  // TODO: Investigate how to fix DM_DEFAULT_ENCODING reported by findbugs in:
  // responseReader = new BufferedReader(new
  // InputStreamReader(response.getEntity()
  // .getContent()));
  private VlcRcStatus executeRequestToVlcServer(String url) {
    logger.trace("Executing request: " + url);
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
    credentialsProvider.setCredentials(AuthScope.ANY, credentials);
    HttpClient client = createHttpClient(credentialsProvider);
    HttpGet request = new HttpGet(url);
    HttpResponse response;
    BufferedReader responseReader = null;
    try {
      response = executeGetRequest(client, request);
      InputStream inputStreamFromResponse = getInputStreamFromResponse(response);
      responseReader = new BufferedReader(new InputStreamReader(inputStreamFromResponse));

      StringBuffer responseBody = new StringBuffer();
      String line = "";
      while ((line = responseReader.readLine()) != null) {
        responseBody.append(line);
      }
      logger.trace("VLC response Status Code: " + getResponseStatusCode(response)
          + ". VLC Response Body: " + responseBody);
      VlcRcStatus vlcRcStatus = buildVlcRcStatus(responseBody.toString());
      return vlcRcStatus;
    } catch (IOException e) {
      e.printStackTrace();
      throw new KameHouseException(e);
    } finally {
      try {
        if (responseReader != null) {
          responseReader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
        logger.error("Unable to close responseReader");
      }
    }
  }
  
  /**
   * Creates an instance of HttpClient with the provided credentials.
   */
  private HttpClient createHttpClient(CredentialsProvider credentialsProvider) {
    return HttpClientBuilder.create().setDefaultCredentialsProvider(
        credentialsProvider).build();
  }

  /**
   * Execute the HTTP Get request to the specified HttpClient.
   */
  private HttpResponse executeGetRequest(HttpClient client, HttpGet getRequest)
      throws ClientProtocolException, IOException {
    return client.execute(getRequest);
  }

  /**
   * Returns the response content as an InputStream.
   */
  private InputStream getInputStreamFromResponse(HttpResponse response)
      throws UnsupportedOperationException, IOException {
    return response.getEntity().getContent();
  }

  /**
   * Returns the status code from an HttpResponse instance.
   */
  private int getResponseStatusCode(HttpResponse response) {
    return response.getStatusLine().getStatusCode();
  }
  
  /**
   * Converts the status information returned by the web API of the VLC Player
   * into its internal representation used in the application.
   */
  private VlcRcStatus buildVlcRcStatus(String vlcStatusResponseStr) {

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode vlcStatusResponseJson = mapper.readTree(vlcStatusResponseStr);
      VlcRcStatus vlcRcStatus = new VlcRcStatus();

      /* Set direct attributes */
      vlcRcStatus.setFullscreen(vlcStatusResponseJson.get("fullscreen").asBoolean());
      vlcRcStatus.setRepeat(vlcStatusResponseJson.get("repeat").asBoolean());
      vlcRcStatus.setSubtitleDelay(vlcStatusResponseJson.get("subtitledelay").asInt());
      vlcRcStatus.setAspectRatio(vlcStatusResponseJson.get("aspectratio").asText());
      vlcRcStatus.setAudioDelay(vlcStatusResponseJson.get("audiodelay").asInt());
      vlcRcStatus.setApiVersion(vlcStatusResponseJson.get("apiversion").asInt());
      vlcRcStatus.setCurrentPlId(vlcStatusResponseJson.get("currentplid").asInt());
      vlcRcStatus.setTime(vlcStatusResponseJson.get("time").asInt());
      vlcRcStatus.setVolume(vlcStatusResponseJson.get("volume").asInt());
      vlcRcStatus.setLength(vlcStatusResponseJson.get("length").asInt());
      vlcRcStatus.setRandom(vlcStatusResponseJson.get("random").asBoolean());
      vlcRcStatus.setRate(vlcStatusResponseJson.get("rate").asInt());
      vlcRcStatus.setState(vlcStatusResponseJson.get("state").asText());
      vlcRcStatus.setLoop(vlcStatusResponseJson.get("loop").asBoolean());
      vlcRcStatus.setPosition(vlcStatusResponseJson.get("position").asInt());
      vlcRcStatus.setVersion(vlcStatusResponseJson.get("version").asText());

      /* Set stats */
      JsonNode statsJson = vlcStatusResponseJson.get("stats");
      Map<String, Object> stats = new HashMap<String, Object>();
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
      vlcRcStatus.setStats(stats);

      /* Set audioFilters */
      Map<String, String> audioFilters = new HashMap<String, String>();
      JsonNode audioFiltersJson = vlcStatusResponseJson.get("audiofilters");
      Iterator<Entry<String, JsonNode>> audioFiltersIterator = audioFiltersJson.fields();
      while (audioFiltersIterator.hasNext()) {
        Entry<String, JsonNode> audioFiltersEntry = audioFiltersIterator.next();
        audioFilters.put(audioFiltersEntry.getKey(), audioFiltersEntry.getValue().asText());
      }
      vlcRcStatus.setAudioFilters(audioFilters);

      /* Set videoEffects */
      Map<String, Integer> videoEffects = new HashMap<String, Integer>();
      JsonNode videoEffectsJson = vlcStatusResponseJson.get("videoeffects");
      Iterator<Entry<String, JsonNode>> videoEffectsIterator = videoEffectsJson.fields();
      while (videoEffectsIterator.hasNext()) {
        Entry<String, JsonNode> videoEffectsEntry = videoEffectsIterator.next();
        videoEffects.put(videoEffectsEntry.getKey(), videoEffectsEntry.getValue().asInt());
      }
      vlcRcStatus.setVideoEffects(videoEffects);

      /* Set equalizer */
      JsonNode equalizerJson = vlcStatusResponseJson.get("equalizer");
      VlcRcStatus.Equalizer equalizer = new VlcRcStatus.Equalizer();

      JsonNode presetsJson = equalizerJson.get("presets");
      if (presetsJson != null) {
        Iterator<Entry<String, JsonNode>> presetsIterator = presetsJson.fields();
        Map<String, String> equalizerPresets = new HashMap<String, String>();
        while (presetsIterator.hasNext()) {
          Entry<String, JsonNode> presetsEntry = presetsIterator.next();
          equalizerPresets.put(presetsEntry.getKey(), presetsEntry.getValue().asText());
        }
        equalizer.setPresets(equalizerPresets);
      }

      JsonNode bandsJson = equalizerJson.get("bands");
      if (bandsJson != null) {
        Iterator<Entry<String, JsonNode>> bandsIterator = bandsJson.fields();
        Map<String, Integer> equalizerBands = new HashMap<String, Integer>();
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

      /* Set information */
      VlcRcStatus.Information information = new VlcRcStatus.Information();
      JsonNode informationJson = vlcStatusResponseJson.get("information");

      information.setChapter(informationJson.get("chapter").asText());
      List<String> chapters = new ArrayList<String>();
      String[] chaptersArray = informationJson.get("chapters").asText().split(",");
      chapters.addAll(Arrays.asList(chaptersArray));
      information.setChapters(chapters);

      information.setTitle(informationJson.get("title").asText());
      List<String> titles = new ArrayList<String>();
      String[] titlesArray = informationJson.get("titles").asText().split(",");
      titles.addAll(Arrays.asList(titlesArray));
      information.setTitles(titles);

      JsonNode categoryJson = informationJson.get("category");
      Iterator<Entry<String, JsonNode>> categoryIterator = categoryJson.fields();
      List<Map<String, Object>> informationCategories = new ArrayList<Map<String, Object>>();
      while (categoryIterator.hasNext()) {
        Map<String, Object> informationCategory = new HashMap<String, Object>();
        Entry<String, JsonNode> categoryEntry = categoryIterator.next();
        String name = categoryEntry.getKey();
        informationCategory.put("name", name);
        if (name.equals("meta")) {
          informationCategory.put("filename", categoryEntry.getValue().get("filename"));
          informationCategory.put("title", categoryEntry.getValue().get("title"));
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
              informationCategory.put("codec", categoryEntry.getValue().get("Codec"));
              informationCategory.put("language", categoryEntry.getValue().get("Language"));
              informationCategory.put("resolution", categoryEntry.getValue().get("Resolution"));
              break;
            case "Audio":
              informationCategory.put("bitrate", categoryEntry.getValue().get("Bitrate"));
              informationCategory.put("channels", categoryEntry.getValue().get("Channels"));
              informationCategory.put("sampleRate", categoryEntry.getValue().get("Sample_rate"));
              informationCategory.put("codec", categoryEntry.getValue().get("Codec"));
              informationCategory.put("language", categoryEntry.getValue().get("Language"));
              break;
            case "Subtitle":
              informationCategory.put("codec", categoryEntry.getValue().get("Codec"));
              informationCategory.put("language", categoryEntry.getValue().get("Language"));
              break;
            default:
              logger.warn("Unrecognized Type returned by VLC: " + type);
              break;
          }
        }
        informationCategories.add(informationCategory);
      }
      information.setCategory(informationCategories);

      vlcRcStatus.setInformation(information);
      return vlcRcStatus;
    } catch (IOException e) {
      e.printStackTrace();
      throw new KameHouseException(e);
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(hostname).append(port).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof VlcPlayer) {
      final VlcPlayer other = (VlcPlayer) obj;
      return new EqualsBuilder().append(hostname, other.getHostname()).append(port, other
          .getPort()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {

    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      e.printStackTrace();
    }
    return "VlcPlayer: INVALID_STATE";
  }
}
