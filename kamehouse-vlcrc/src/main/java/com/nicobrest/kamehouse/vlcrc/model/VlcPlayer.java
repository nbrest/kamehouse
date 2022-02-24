package com.nicobrest.kamehouse.vlcrc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import com.nicobrest.kamehouse.commons.model.KameHouseEntity;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.HttpClientUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.utils.VlcRcStatusBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a VLC Player in the system. It connects to the web API of the VLC Player that it
 * represents to execute commands on it and retrieve the status of the player.
 *
 * @author nbrest
 */
@Entity
@Table(name = "vlc_player")
public class VlcPlayer implements KameHouseEntity<VlcPlayerDto>, Serializable {

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

  @Override
  public VlcPlayerDto buildDto() {
    VlcPlayerDto dto = new VlcPlayerDto();
    dto.setId(getId());
    dto.setHostname(geVlcServerHostname());
    dto.setPort(getPort());
    dto.setUsername(getUsername());
    dto.setPassword(getPassword());
    return dto;
  }

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
   * Executes a command in the VLC Player and return it's status.
   */
  public VlcRcStatus execute(VlcRcCommand command) {
    String commandUrl = buildCommandUrl(command);
    if (commandUrl != null) {
      String vlcServerResponse = execRequestToVlcServer(commandUrl);
      VlcRcStatus vlcRcStatus = VlcRcStatusBuilder.build(vlcServerResponse);
      logVlcRcStatus(vlcRcStatus);
      return vlcRcStatus;
    } else {
      return null;
    }
  }

  /**
   * Gets the status information of the VLC Player.
   */
  @JsonIgnore
  public VlcRcStatus getVlcRcStatus() {
    StringBuilder statusUrl = new StringBuilder();
    statusUrl.append(PROTOCOL);
    statusUrl.append(geVlcServerHostname());
    statusUrl.append(":");
    statusUrl.append(port);
    statusUrl.append(STATUS_URL);
    VlcRcStatus vlcRcStatus = null;
    String vlcServerResponse = execRequestToVlcServer(statusUrl.toString());
    vlcRcStatus = VlcRcStatusBuilder.build(vlcServerResponse);
    logVlcRcStatus(vlcRcStatus);
    return vlcRcStatus;
  }

  /**
   * Gets the current playlist.
   */
  @JsonIgnore
  public List<VlcRcPlaylistItem> getPlaylist() {
    StringBuilder playlistUrl = new StringBuilder();
    playlistUrl.append(PROTOCOL);
    playlistUrl.append(geVlcServerHostname());
    playlistUrl.append(":");
    playlistUrl.append(port);
    playlistUrl.append(PLAYLIST_URL);
    String vlcServerResponse = execRequestToVlcServer(playlistUrl.toString());
    List<VlcRcPlaylistItem> playlist = buildVlcRcPlaylist(vlcServerResponse);
    LOGGER.trace("Response from VLC: playlist - size: {}", playlist.size());
    return playlist;
  }

  /**
   * Browses through the server running vlc.
   */
  public List<VlcRcFileListItem> browse(String uri) {
    StringBuilder browseUrl = new StringBuilder();
    browseUrl.append(PROTOCOL);
    browseUrl.append(geVlcServerHostname());
    browseUrl.append(":");
    browseUrl.append(port);
    browseUrl.append(BROWSE_URL);
    if (uri != null) {
      browseUrl.append("?uri=" + HttpClientUtils.urlEncode(uri));
    } else {
      browseUrl.append("?uri=file:///");
    }
    String vlcServerResponse = execRequestToVlcServer(browseUrl.toString());
    List<VlcRcFileListItem> filelist = buildVlcRcFilelist(vlcServerResponse);
    LOGGER.trace("browse {} in {} filelist size {}", uri, geVlcServerHostname(), filelist.size());
    return filelist;
  }

  /**
   * Returns the hostname of the server running the vlc player taking into consideration whether
   * it's running on a docker container and should control the host or not.
   */
  private String geVlcServerHostname() {
    if (LOCALHOST.equals(hostname) && DockerUtils.shouldControlDockerHost()) {
      return DockerUtils.getDockerHostIp();
    }
    return hostname;
  }

  /**
   * Builds the URL to execute the command in the VLC Player through its web API.
   */
  private String buildCommandUrl(VlcRcCommand command) {
    String encodedCommand = HttpClientUtils.urlEncode(command.getName());
    if (encodedCommand == null) {
      return null;
    }
    StringBuilder commandUrl = new StringBuilder();
    commandUrl.append(PROTOCOL);
    commandUrl.append(geVlcServerHostname());
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
   * Executes a request to the web API of the VLC Player using the provided URL and returns the
   * payload as a String.
   */
  private String execRequestToVlcServer(String url) {
    HttpClient client = HttpClientUtils.getClient(username, password);
    HttpGet request = HttpClientUtils.httpGet(url);
    HttpResponse response;
    LOGGER.trace("Request to VLC: {}", url);
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
      if (LOGGER.isTraceEnabled()) { // only log this ERROR when TRACE is enabled
        LOGGER.error("Error executing request. Message: {}", e.getMessage());
      }
      return null;
    }
  }

  /**
   * Converts the playlist returned by the VLC Player into an internal playlist format.
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
      LOGGER.error("Unable to build VlcRC playlist", e);
      throw new KameHouseException(e.getMessage(), e);
    }
  }

  /**
   * Iterates through the JsonNode array and generate the VlcRcPlaylist.
   */
  private List<VlcRcPlaylistItem> getVlcRcPlaylistFromJsonNode(JsonNode playlistArrayNode) {
    List<VlcRcPlaylistItem> vlcRcPlaylist = new ArrayList<>();
    for (JsonNode jsonNode : playlistArrayNode) {
      VlcRcPlaylistItem playlistItem = new VlcRcPlaylistItem();
      playlistItem.setId(JsonUtils.getInt(jsonNode, "id"));
      playlistItem.setName(JsonUtils.getText(jsonNode, "name"));
      String uri = JsonUtils.getText(jsonNode, "uri");
      playlistItem.setUri(uri);
      playlistItem.setFilename(getFilenameFromUri(uri));
      playlistItem.setDuration(JsonUtils.getInt(jsonNode, "duration"));
      vlcRcPlaylist.add(playlistItem);
    }
    return vlcRcPlaylist;
  }

  private String getFilenameFromUri(String uri) {
    String decodedUri = HttpClientUtils.urlDecode(uri);
    if (decodedUri != null && decodedUri.startsWith(FILE_PROTOCOL)) {
      decodedUri = decodedUri.replace(FILE_PROTOCOL, "");
    }
    return decodedUri;
  }

  /**
   * Converts the file list returned by the VLC Player into an internal file list format.
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
      LOGGER.error("Unable to build VlcRC file list", e);
      throw new KameHouseException(e.getMessage(), e);
    }
  }

  /**
   * Log vlcRcStatus response.
   */
  private void logVlcRcStatus(VlcRcStatus vlcRcStatus) {
    if (vlcRcStatus != null
        && vlcRcStatus.getInformation() != null
        && vlcRcStatus.getInformation().getMeta() != null) {
      LOGGER.trace(
          "Response from VLC: status - filename: {}",
          vlcRcStatus.getInformation().getMeta().getFilename());
    } else {
      LOGGER.trace("Response from VLC: status - No filename present in the response");
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
