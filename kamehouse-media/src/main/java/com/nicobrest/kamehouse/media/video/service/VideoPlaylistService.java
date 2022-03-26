package com.nicobrest.kamehouse.media.video.service;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand.Output;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.SshClientUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service to manage the video playlists in the local system.
 *
 * @author nbrest
 */
@Service
public class VideoPlaylistService {

  public static final String PROP_MEDIA_SERVER_NAME = "media.server.name";
  public static final String PROP_PLAYLISTS_PATH_LINUX = "playlists.path.linux";
  public static final String PROP_PLAYLISTS_PATH_REMOTE_HTTP = "playlists.path.remote.http";
  public static final String PROP_PLAYLISTS_PATH_WINDOWS = "playlists.path.windows";
  private static final String SUPPORTED_PLAYLIST_EXTENSION = ".m3u";
  private static final String VIDEO_PLAYLIST_CACHE = "videoPlaylist";
  private static final String VIDEO_PLAYLISTS_CACHE = "videoPlaylists";
  private static final String ANIME = "anime";
  private static final String CARTOONS = "cartoons";

  private static final Logger logger = LoggerFactory.getLogger(VideoPlaylistService.class);

  /**
   * Gets all video playlists without their contents.
   */
  @Cacheable(value = VIDEO_PLAYLISTS_CACHE)
  public List<Playlist> getAll() {
    return getAll(false);
  }

  /**
   * Gets all video playlists specifying if it should get the contents of each playlist or not.
   */
  public List<Playlist> getAll(Boolean fetchContent) {
    logger.trace("getAll");
    if (DockerUtils.shouldControlDockerHost()) {
      return getAllFromDockerHost(fetchContent);
    }
    return getAllFromFileSystem(fetchContent);
  }

  /**
   * Get the specified playlist.
   */
  @Cacheable(value = VIDEO_PLAYLIST_CACHE)
  public Playlist getPlaylist(String playlistFilename, boolean fetchContent) {
    logger.trace("Get playlist {}", playlistFilename);
    if (DockerUtils.shouldControlDockerHost()) {
      return getPlaylistFromDockerHost(playlistFilename, fetchContent);
    }
    return getPlaylistLocal(playlistFilename, fetchContent);
  }

  /**
   * Get the specified playlist from the docker host.
   */
  private static Playlist getPlaylistFromDockerHost(String playlistFilename, Boolean fetchContent) {
    logger.trace("getPlaylistFromDockerHost {}", playlistFilename);
    Path basePlaylistsPath = getBasePlaylistsPath();
    Playlist playlist = new Playlist();
    String name = StringUtils.substringAfterLast(playlistFilename,
        FileUtils.getHostPathSeparator());
    playlist.setName(name);
    String category = getCategoryFromDockerHost(basePlaylistsPath.toString(), playlistFilename);
    playlist.setCategory(category);
    playlist.setPath(playlistFilename);
    if (fetchContent) {
      List<String> playlistContent = getPlaylistContentFromDockerHost(playlistFilename);
      playlist.setFiles(playlistContent);
    }
    logger.trace("Get playlist {} response {}", playlistFilename, playlist);
    return playlist;
  }

  /**
   * Get the specified playlist from the local filesystem.
   */
  private static Playlist getPlaylistLocal(String playlistFilename, Boolean fetchContent) {
    logger.trace("getPlaylistLocal {}", playlistFilename);
    Path playlistPath = Paths.get(playlistFilename);
    if (!isValidPlaylist(playlistPath)) {
      logger.error(
          "Invalid playlist path specified. Check the validations for supported " + "playlists");
      return null;
    }
    Playlist playlist = null;
    Path basePlaylistsPath = getBasePlaylistsPath();
    Path playlistFileNamePath = playlistPath.getFileName();
    if (playlistFileNamePath != null) {
      playlist = new Playlist();
      playlist.setName(playlistFileNamePath.toString());
      String category = getCategory(basePlaylistsPath, playlistPath);
      playlist.setCategory(category);
      playlist.setPath(playlistPath.toString());
      if (fetchContent) {
        List<String> playlistContent = getPlaylistContent(playlistPath.toString());
        playlist.setFiles(playlistContent);
      }
    }
    logger.trace("Get playlist {} response {}", playlistPath, playlist);
    return playlist;
  }

  /**
   * Get all playlists from the docker container host.
   */
  private List<Playlist> getAllFromDockerHost(Boolean fetchContent) {
    logger.trace("getAllFromDockerHost");
    Path basePlaylistPath = getBasePlaylistsPath();
    SystemCommand listPlaylistsCommand = new SystemCommand() {
      @Override
      public String getCommandForSsh() {
        if (DockerUtils.isWindowsDockerHost()) {
          String command = "powershell.exe -c \"cd " + basePlaylistPath
              + "; Get-ChildItem -Recurse -Filter '*.m3u' | Select FullName\"";
          command = command.replace("/", "\\");
          return command;
        }
        return "find " + basePlaylistPath + " | grep -e  \"m3u\" | sort";
      }
    };
    Output output = SshClientUtils.executeShell(DockerUtils.getDockerHostIp(),
        DockerUtils.getDockerHostUsername(),
        listPlaylistsCommand, DockerUtils.isWindowsDockerHost());
    List<Playlist> playlists = new ArrayList<>();
    String sshShellOutput = output.getStandardOutput().get(0);
    if (StringUtils.isEmpty(sshShellOutput)) {
      return playlists;
    }
    List<String> playlistFilePaths = getPlaylistFilePaths(sshShellOutput);
    for (String playlistFilePath : playlistFilePaths) {
      Playlist playlist = getPlaylist(playlistFilePath, fetchContent);
      if (playlist != null) {
        playlists.add(playlist);
      }
    }
    return playlists;
  }

  /**
   * Get the file paths for all the playlists from the ssh's shell output.
   */
  private static List<String> getPlaylistFilePaths(String sshShellOutput) {
    if (DockerUtils.isWindowsDockerHost()) {
      logger.trace("Windows shell");
      sshShellOutput = StringUtils.substringAfter(sshShellOutput, "--------");
      List<String> playlistFilePaths = Arrays.stream(sshShellOutput.split("\r\n"))
          .filter(e -> e.contains(".m3u"))
          .map(e -> StringUtils.substringBeforeLast(e, ".m3u") + ".m3u")
          .collect(Collectors.toList());
      logger.trace(playlistFilePaths.toString());
      return playlistFilePaths;
    } else {
      logger.trace("Linux shell");
      List<String> playlistFilePaths = Arrays.stream(sshShellOutput.split("\n"))
          .filter(e -> e.contains(".m3u"))
          .map(e -> StringUtils.substringBeforeLast(e, ".m3u") + ".m3u")
          .collect(Collectors.toList());
      logger.trace(playlistFilePaths.toString());
      return playlistFilePaths;
    }
  }

  /**
   * Get all playlists from the local filesystem.
   */
  private List<Playlist> getAllFromFileSystem(Boolean fetchContent) {
    logger.trace("getAllFromFileSystem");
    Path basePlaylistPath = getBasePlaylistsPath();
    List<Playlist> videoPlaylists = new ArrayList<>();
    try (Stream<Path> filePaths = Files.walk(basePlaylistPath)) {
      Iterator<Path> filePathsIterator = filePaths.iterator();
      while (filePathsIterator.hasNext()) {
        Path playlistPath = filePathsIterator.next();
        if (!playlistPath.toFile().isDirectory()) {
          Playlist playlist = getPlaylist(playlistPath.toString(), fetchContent);
          if (playlist != null) {
            videoPlaylists.add(playlist);
          }
        }
      }
    } catch (IOException e) {
      logger.error("An error occurred while getting all the video playlists", e);
    }
    videoPlaylists.sort(new Playlist.Comparator());
    logger.trace("getAll response {}", videoPlaylists);
    return videoPlaylists;
  }

  /**
   * Get the base path where to look for playlists.
   */
  private static Path getBasePlaylistsPath() {
    String userHome = DockerUtils.getUserHome();
    String mediaServer = PropertiesUtils.getProperty(PROP_MEDIA_SERVER_NAME);
    String playlistsPath;
    if (isMediaServerLocalhost(mediaServer)) {
      if (DockerUtils.isWindowsHostOrWindowsDockerHost()) {
        playlistsPath = PropertiesUtils.getProperty(PROP_PLAYLISTS_PATH_WINDOWS);
      } else {
        playlistsPath = PropertiesUtils.getProperty(PROP_PLAYLISTS_PATH_LINUX);
      }
    } else {
      playlistsPath = PropertiesUtils.getProperty(PROP_PLAYLISTS_PATH_REMOTE_HTTP);
    }
    String videoPlaylistsHome = userHome + playlistsPath;
    if (DockerUtils.isWindowsHostOrWindowsDockerHost()) {
      videoPlaylistsHome = videoPlaylistsHome.replace("/", "\\");
    }
    return Paths.get(videoPlaylistsHome);
  }

  /**
   * Checks if the current server running kamehouse is the media server.
   */
  private static boolean isMediaServerLocalhost(String mediaServer) {
    if (StringUtils.isEmpty(mediaServer)) {
      return false;
    }
    return mediaServer.equalsIgnoreCase(PropertiesUtils.getHostname())
        || (DockerUtils.shouldControlDockerHost()
        && mediaServer.equalsIgnoreCase(DockerUtils.getDockerHostHostname()));
  }

  /**
   * Gets the category of the playlist based on the base path.
   */
  private static String getCategory(Path basePath, Path filePath) {
    String absoluteBasePath = sanitizePath(basePath.toFile().getAbsolutePath());
    int basePathLength = absoluteBasePath.length();
    Path parentPath = filePath.getParent();
    if (parentPath != null) {
      String absoluteParentFilePath = sanitizePath(parentPath.toFile().getAbsolutePath());
      String category = absoluteParentFilePath.substring(basePathLength + 1);
      if (category.startsWith(ANIME)) {
        return ANIME;
      }
      if (category.startsWith(CARTOONS)) {
        return CARTOONS;
      }
      return category;
    } else {
      return null;
    }
  }

  /**
   * Gets the category of the playlist based on the base path.
   */
  private static String getCategoryFromDockerHost(String absoluteBasePath, String filePath) {
    int basePathLength = absoluteBasePath.length();
    String absoluteParentFilePath = StringUtils.substringBeforeLast(filePath,
        FileUtils.getHostPathSeparator());
    String category = absoluteParentFilePath.substring(basePathLength + 1);
    logger.trace("catetory: {}", category);
    if (category.startsWith(ANIME)) {
      return ANIME;
    }
    if (category.startsWith(CARTOONS)) {
      return CARTOONS;
    }
    return category;
  }

  /**
   * Clean up path from unnecessary jumps such as '/./' or '\.\'.
   */
  private static String sanitizePath(String path) {
    return path.replaceAll("\\\\.\\\\", "\\\\").replaceAll("/./", "/");
  }

  /**
   * Validate that the playlist specified is valid.
   */
  private static boolean isValidPlaylist(Path playlistPath) {
    // Check that file exists
    if (!playlistPath.toFile().exists()) {
      return false;
    }
    // Check that the playlist has a supported extension
    String playlistPathString = playlistPath.toString();
    String extension = playlistPathString.substring(playlistPathString.length() - 4);
    if (!SUPPORTED_PLAYLIST_EXTENSION.equalsIgnoreCase(extension)) {
      return false;
    }
    // Check that the playlist path doesn't contain double dots, to jump out of the root path
    if (playlistPathString.contains(File.separator + ".." + File.separator)) {
      return false;
    }
    return true;
  }

  /**
   * Get the files in the playlist for the specified file (absolute or relative path).
   */
  private static List<String> getPlaylistContent(String playlistFilename) {
    List<String> files = null;
    logger.trace("Getting content for playlist {}", playlistFilename);
    try {
      files =
          Files.readAllLines(Paths.get(playlistFilename)).stream()
              // Remove comments of m3u files
              .filter(file -> !file.startsWith("#"))
              // Remove empty lines
              .filter(file -> !file.trim().isEmpty())
              .collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error reading {} content", playlistFilename, e);
    }
    return files;
  }

  /**
   * Get the contents of the specified playlist file.
   */
  private static List<String> getPlaylistContentFromDockerHost(String playlistFilename) {
    logger.trace("Getting content for playlist {}", playlistFilename);
    SystemCommand getPlaylistContentCommand = new SystemCommand() {
      @Override
      public String getCommandForSsh() {
        if (DockerUtils.isWindowsDockerHost()) {
          String command = "powershell.exe -c \"cat " + playlistFilename + "\"";
          command = command.replace("/", "\\");
          return command;
        }
        return "cat " + playlistFilename + " | sort";
      }
    };
    Output output = SshClientUtils.executeShell(DockerUtils.getDockerHostIp(),
        DockerUtils.getDockerHostUsername(), getPlaylistContentCommand,
        DockerUtils.isWindowsDockerHost());
    String sshShellOutput = output.getStandardOutput().get(0);
    sshShellOutput = StringUtils.substringAfter(sshShellOutput, "#EXTM3U");
    String[] sshShellOutputArray;
    if (DockerUtils.isWindowsDockerHost()) {
      sshShellOutputArray = sshShellOutput.split("\r\n");
    } else {
      sshShellOutputArray = sshShellOutput.split("\n");
    }
    List<String> playlistContent = Arrays.stream(sshShellOutputArray)
        .filter(file -> !StringUtils.isEmpty(file))
        .filter(file -> !StringUtils.isEmpty(file.trim()))
        .filter(file -> !file.startsWith("#"))
        .filter(file -> !file.contains("conhost.exe"))
        .filter(file -> !file.trim().endsWith("logout"))
        .map(file -> removeCharactersPastFileExtension(file))
        .collect(Collectors.toList());
    logger.trace(playlistContent.toString());
    return playlistContent;
  }

  /**
   * Remove the strange characters added after the file extension during the ssh session.
   */
  private static String removeCharactersPastFileExtension(String file) {
    return StringUtils.substring(file, 0, StringUtils.lastIndexOf(file, ".") + 4);
  }
}
