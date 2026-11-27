package com.nicobrest.kamehouse.ztvrc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.ztvrc.utils.ZtvRcStatusBuilder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a ZTV Player in the system. It connects to the web API of the ZTV Player that it
 * represents to execute commands on it and retrieve the status of the player.
 *
 * @author nbrest
 */
@Entity
@Table(name = "vlc_player")
public class VlcPlayer implements ZtvPlayer, Serializable {

  @JsonIgnore
  private static final long serialVersionUID = 1L;
  @JsonIgnore
  private static final Logger LOGGER = LoggerFactory.getLogger(VlcPlayer.class);
  @JsonIgnore
  private static final String PROTOCOL = "http://";
  @JsonIgnore
  private static final String STATUS_URL = "/requests/status.json";
  @JsonIgnore
  private static final String PLAYLIST_URL = "/requests/playlist.json";
  @JsonIgnore
  private static final String BROWSE_URL = "/requests/browse.json";
  @JsonIgnore
  private static final String FILE_PROTOCOL = "file://";
  @JsonIgnore
  private static final String LOCALHOST = "localhost";
  @JsonIgnore
  private static final String ERROR_CONNECTING_TO_ZTV = "failed: Connection refused";
  @JsonIgnore
  private static final String ERROR_STATUS_NOT_FOUND = "HTTP Status 404";

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "hostname", unique = true, nullable = false)
  private String hostname;

  @Column(name = "port")
  private int port;

  @Column(name = "username")
  private String username;

  @Masked
  @Column(name = "password")
  private String password;

  public VlcPlayer() {
    super();
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  @Override
  public String getHostname() {
    return hostname;
  }

  @Override
  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public int getPort() {
    return port;
  }

  @Override
  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Executes a command in the ZTV Player and return it's status.
   */
  @Override
  public ZtvRcStatus execute(ZtvRcCommand command) {
    String commandUrl = buildCommandUrl(command);
    if (commandUrl == null) {
      return null;
    }
    String ztvServerResponse = execRequestToZtvServer(commandUrl);
    if (StringUtils.isEmpty(ztvServerResponse)) {
      return null;
    }
    ZtvRcStatus ztvRcStatus = ZtvRcStatusBuilder.build(ztvServerResponse);
    logZtvRcStatus(ztvRcStatus);
    return ztvRcStatus;
  }

  /**
   * Gets the status information of the ZTV Player.
   */
  @JsonIgnore
  @Override
  public ZtvRcStatus getZtvRcStatus() {
    StringBuilder statusUrl = new StringBuilder();
    statusUrl.append(PROTOCOL);
    statusUrl.append(getZtvServerHostname());
    statusUrl.append(":");
    statusUrl.append(port);
    statusUrl.append(STATUS_URL);
    ZtvRcStatus ztvRcStatus = null;
    String ztvServerResponse = execRequestToZtvServer(statusUrl.toString());
    ztvRcStatus = ZtvRcStatusBuilder.build(ztvServerResponse);
    logZtvRcStatus(ztvRcStatus);
    return ztvRcStatus;
  }

  /**
   * Gets the current playlist.
   */
  @JsonIgnore
  @Override
  public List<ZtvRcPlaylistItem> getPlaylist() {
    StringBuilder playlistUrl = new StringBuilder();
    playlistUrl.append(PROTOCOL);
    playlistUrl.append(getZtvServerHostname());
    playlistUrl.append(":");
    playlistUrl.append(port);
    playlistUrl.append(PLAYLIST_URL);
    String ztvServerResponse = execRequestToZtvServer(playlistUrl.toString());
    List<ZtvRcPlaylistItem> playlist = buildZtvRcPlaylist(ztvServerResponse);
    LOGGER.trace("Response from ZTV: playlist - size: {}", playlist.size());
    return playlist;
  }

  /**
   * Browses through the ZTV Player server.
   */
  @Override
  public List<ZtvRcFileListItem> browse(String uri) {
    StringBuilder browseUrl = new StringBuilder();
    browseUrl.append(PROTOCOL);
    browseUrl.append(getZtvServerHostname());
    browseUrl.append(":");
    browseUrl.append(port);
    browseUrl.append(BROWSE_URL);
    if (uri != null) {
      browseUrl.append("?uri=" + HttpClientUtils.urlEncode(uri));
    } else {
      browseUrl.append("?uri=file:///");
    }
    String ztvServerResponse = execRequestToZtvServer(browseUrl.toString());
    List<ZtvRcFileListItem> filelist = buildZtvRcFilelist(ztvServerResponse);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("browse {} in {} filelist size {}", uri,
          getZtvServerHostname(), filelist.size());
    }
    return filelist;
  }

  /**
   * Returns the hostname of the server running the ZTV Player taking into consideration whether
   * it's running on a docker container and should control the host or not.
   */
  private String getZtvServerHostname() {
    if (LOCALHOST.equals(hostname) && DockerUtils.shouldControlDockerHost()) {
      return DockerUtils.getDockerHostIp();
    }
    return hostname;
  }

  /**
   * Builds the URL to execute the command in the ZTV Player through its web API.
   */
  private String buildCommandUrl(ZtvRcCommand command) {
    String encodedCommand = HttpClientUtils.urlEncode(command.getName());
    if (encodedCommand == null) {
      return null;
    }
    StringBuilder commandUrl = new StringBuilder();
    commandUrl.append(PROTOCOL);
    commandUrl.append(getZtvServerHostname());
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
   * Executes a request to the web API of the ZTV Player using the provided URL and returns the
   * payload as a String.
   */
  private String execRequestToZtvServer(String url) {
    HttpClient client = HttpClientUtils.getClient(username, password, true);
    HttpGet request = HttpClientUtils.httpGet(url);
    HttpResponse response;
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Request to ZTV: {}", StringUtils.sanitize(url));
    }
    try {
      response = HttpClientUtils.execRequest(client, request);
      try (InputStream resInStream = HttpClientUtils.getInputStream(response);
          BufferedReader responseReader =
              new BufferedReader(new InputStreamReader(resInStream, StandardCharsets.UTF_8))) {
        StringBuilder responseBody = new StringBuilder();
        String line = "";
        while ((line = responseReader.readLine()) != null) {
          responseBody.append(line);
        }
        return responseBody.toString();
      }
    } catch (IOException e) {
      if (e.getMessage().contains(ERROR_CONNECTING_TO_ZTV)) {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Error connecting to ZTV Player. Message: {}", e.getMessage());
        }
      } else {
        LOGGER.error("Error executing request. Message: {}", e.getMessage());
      }
      return null;
    }
  }

  /**
   * Converts the playlist returned by the ZTV Player into an internal playlist format.
   */
  private List<ZtvRcPlaylistItem> buildZtvRcPlaylist(String ztvRcPlaylistResponse) {
    List<ZtvRcPlaylistItem> ztvRcPlaylist = new ArrayList<>();
    if (ztvRcPlaylistResponse == null) {
      return ztvRcPlaylist;
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode ztvRcPlaylistResponseJson = mapper.readTree(ztvRcPlaylistResponse);
      JsonNode firstChildrenArray = ztvRcPlaylistResponseJson.get("children");
      if (!JsonUtils.isJsonNodeArrayEmpty(firstChildrenArray)) {
        for (JsonNode firstChildrenNode : firstChildrenArray) {
          if ("Playlist".equals(JsonUtils.getText(firstChildrenNode, "name"))) {
            JsonNode playlistArrayNode = firstChildrenNode.get("children");
            if (!JsonUtils.isJsonNodeArrayEmpty(playlistArrayNode)) {
              ztvRcPlaylist = getZtvRcPlaylistFromJsonNode(playlistArrayNode);
            }
          }
        }
      }
    } catch (IOException e) {
      handleBuildZtvRcPlaylistException(e);
    }
    return ztvRcPlaylist;
  }

  /**
   * Handle exception building the playlist.
   */
  private void handleBuildZtvRcPlaylistException(IOException exception) {
    if (exception.getMessage().contains(ERROR_STATUS_NOT_FOUND)) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Error connecting to ZTV Player. Message: {}", exception.getMessage());
      }
    } else {
      LOGGER.error("Unable to build ZtvRC playlist. Message: {}", exception.getMessage());
    }
  }

  /**
   * Iterates through the JsonNode array and generate the ZtvRcPlaylist.
   */
  private List<ZtvRcPlaylistItem> getZtvRcPlaylistFromJsonNode(JsonNode playlistArrayNode) {
    List<ZtvRcPlaylistItem> ztvRcPlaylist = new ArrayList<>();
    for (JsonNode jsonNode : playlistArrayNode) {
      ZtvRcPlaylistItem playlistItem = new ZtvRcPlaylistItem();
      playlistItem.setId(JsonUtils.getInt(jsonNode, "id"));
      playlistItem.setName(JsonUtils.getText(jsonNode, "name"));
      String uri = JsonUtils.getText(jsonNode, "uri");
      playlistItem.setUri(uri);
      playlistItem.setFilename(getFilenameFromUri(uri));
      playlistItem.setDuration(JsonUtils.getInt(jsonNode, "duration"));
      ztvRcPlaylist.add(playlistItem);
    }
    return ztvRcPlaylist;
  }

  private String getFilenameFromUri(String uri) {
    String decodedUri = HttpClientUtils.urlDecode(uri);
    if (decodedUri != null && decodedUri.startsWith(FILE_PROTOCOL)) {
      decodedUri = decodedUri.replace(FILE_PROTOCOL, "");
    }
    return decodedUri;
  }

  /**
   * Converts the file list returned by the ZTV Player into an internal file list format.
   */
  private List<ZtvRcFileListItem> buildZtvRcFilelist(String ztvRcFileListResponse) {
    List<ZtvRcFileListItem> ztvRcFilelist = new ArrayList<>();
    if (ztvRcFileListResponse == null) {
      return ztvRcFilelist;
    }
    String parsedZtvRcPlaylistResponse = ztvRcFileListResponse.replace("\\", "/");
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode ztvRcFileListResponseJson = mapper.readTree(parsedZtvRcPlaylistResponse);
      JsonNode elementArray = ztvRcFileListResponseJson.get("element");
      if (elementArray != null && elementArray.isArray()) {
        for (JsonNode jsonNode : elementArray) {
          ZtvRcFileListItem fileListItem = new ZtvRcFileListItem();
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
          ztvRcFilelist.add(fileListItem);
        }
      }
    } catch (IOException e) {
      handleBuildZtvRcFilelistIoException(e);
    }
    return ztvRcFilelist;
  }

  /**
   * Handle IOException.
   */
  private void handleBuildZtvRcFilelistIoException(IOException exception) {
    LOGGER.error("Unable to build ZtvRC file list. Message {}", exception.getMessage());
  }

  /**
   * Log ztvRcStatus response.
   */
  private void logZtvRcStatus(ZtvRcStatus ztvRcStatus) {
    if (ztvRcStatus != null
        && ztvRcStatus.getInformation() != null
        && ztvRcStatus.getInformation().getMeta() != null) {
      LOGGER.trace(
          "Response from ZTV: status - filename: {}",
          ztvRcStatus.getInformation().getMeta().getFilename());
    } else {
      LOGGER.trace("Response from ZTV: status - No filename present in the response");
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(hostname).append(port).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof VlcPlayer other) {
      return new EqualsBuilder()
          .append(id, other.getId())
          .append(hostname, other.getHostname())
          .append(port, other.getPort())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
