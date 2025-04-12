package com.nicobrest.kamehouse.media.video.service;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.model.kamehousecommand.GetPlaylistContentKameHouseCommand;
import com.nicobrest.kamehouse.media.video.model.kamehousecommand.ListPlaylistsKameHouseCommand;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

  public static final String DEFAULT_PLAYLISTS_PATH =
      ".kamehouse/data/playlists";
  public static final String PROP_PLAYLISTS_PATH = "playlists.path";
  private static final String SUPPORTED_PLAYLIST_EXTENSION = ".m3u";
  private static final String VIDEO_PLAYLIST_CACHE = "videoPlaylist";
  private static final String VIDEO_PLAYLISTS_CACHE = "videoPlaylists";

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
  public List<Playlist> getAll(boolean fetchContent) {
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
  private static Playlist getPlaylistFromDockerHost(String playlistFilename, boolean fetchContent) {
    playlistFilename = playlistFilename.replace("\\", "/");
    logger.trace("getPlaylistFromDockerHost {}", playlistFilename);
    Path basePlaylistsPath = getBasePlaylistsPathFromDockerHost();
    Playlist playlist = new Playlist();
    String name = StringUtils.substringAfterLast(playlistFilename, "/");
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
  private static Playlist getPlaylistLocal(String playlistFilename, boolean fetchContent) {
    logger.trace("getPlaylistLocal {}", playlistFilename);
    Path playlistPath = Paths.get(playlistFilename);
    if (!isValidPlaylist(playlistPath)) {
      logger.error(
          "Invalid playlist path specified. Check the validations for supported playlists");
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
  private List<Playlist> getAllFromDockerHost(boolean fetchContent) {
    logger.trace("getAllFromDockerHost");
    ListPlaylistsKameHouseCommand listPlaylistsCommand = new ListPlaylistsKameHouseCommand();
    KameHouseCommandResult kameHouseCommandResult = DockerUtils.executeOnDockerHost(
        listPlaylistsCommand);
    List<Playlist> playlists = new ArrayList<>();
    List<String> standardOutput = kameHouseCommandResult.getStandardOutput();
    if (standardOutput == null || standardOutput.isEmpty()) {
      return playlists;
    }
    List<String> playlistFilePaths = getPlaylistFilePaths(standardOutput);
    for (String playlistFilePath : playlistFilePaths) {
      Playlist playlist = getPlaylist(playlistFilePath, fetchContent);
      if (playlist != null) {
        playlists.add(playlist);
      }
    }
    return playlists;
  }

  /**
   * Get the file paths for all the playlists from the remote shell kameHouseCommandResult.
   */
  private static List<String> getPlaylistFilePaths(List<String> standardOutput) {
    List<String> playlistFilePaths = standardOutput.stream()
        .filter(line -> line.contains(".m3u"))
        .map(line -> StringUtils.substringBeforeLast(line, ".m3u") + ".m3u")
        .toList();
    if (logger.isTraceEnabled()) {
      logger.trace(playlistFilePaths.toString());
    }
    return playlistFilePaths;
  }

  /**
   * Get all playlists from the local filesystem.
   */
  private List<Playlist> getAllFromFileSystem(boolean fetchContent) {
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
    String playlistsPath = PropertiesUtils.getProperty(PROP_PLAYLISTS_PATH, DEFAULT_PLAYLISTS_PATH);
    String videoPlaylistsHome = userHome + "/" + playlistsPath;
    if (DockerUtils.isWindowsHostOrWindowsDockerHost()) {
      videoPlaylistsHome = videoPlaylistsHome.replace("/", "\\");
    }
    return Paths.get(videoPlaylistsHome);
  }

  /**
   * Get the base path where to look for playlists from the docker host.
   */
  private static Path getBasePlaylistsPathFromDockerHost() {
    String userHome = DockerUtils.getUserHome();
    String playlistsPath = DockerUtils.getDockerHostPlaylistPath();
    String videoPlaylistsHome = userHome + "/" + playlistsPath;
    if (DockerUtils.isWindowsHostOrWindowsDockerHost()) {
      videoPlaylistsHome = videoPlaylistsHome.replace("/", "\\");
    }
    return Paths.get(videoPlaylistsHome);
  }

  /**
   * Gets the category of the playlist based on the base path.
   */
  private static String getCategory(Path basePath, Path filePath) {
    if (filePath.getParent() == null) {
      return null;
    }
    Path fileParentPath = filePath.getParent();
    if (fileParentPath == null) {
      return null;
    }
    Path categoryPath = fileParentPath.getParent();
    if (categoryPath == null) {
      return null;
    }
    String categoryAbsoluteFilePath = sanitizePath(categoryPath.toFile().getAbsolutePath());
    String absoluteBasePath = sanitizePath(basePath.toFile().getAbsolutePath());
    int basePathLength = absoluteBasePath.length();
    return categoryAbsoluteFilePath.substring(basePathLength + 1);
  }

  /**
   * Gets the category of the playlist based on the base path.
   */
  private static String getCategoryFromDockerHost(String absoluteBasePath, String filePath) {
    absoluteBasePath = absoluteBasePath.replace("\\", "/");
    int basePathLength = absoluteBasePath.length();
    String absoluteParentFilePath = StringUtils.substringBeforeLast(filePath, "/");
    String categoryFilePath = StringUtils.substringBeforeLast(absoluteParentFilePath, "/");
    return categoryFilePath.substring(basePathLength + 1);
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
    return !playlistPathString.contains(File.separator + ".." + File.separator);
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
              .toList();
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
    GetPlaylistContentKameHouseCommand getPlaylistContentCommand =
        new GetPlaylistContentKameHouseCommand(playlistFilename);
    KameHouseCommandResult kameHouseCommandResult = DockerUtils.executeOnDockerHost(
        getPlaylistContentCommand);
    List<String> standardOutput = kameHouseCommandResult.getStandardOutput();
    List<String> playlistContent = standardOutput.stream()
        .filter(line -> !StringUtils.isEmpty(line))
        .filter(line -> !StringUtils.isEmpty(line.trim()))
        .filter(line -> !line.startsWith("#"))
        .filter(line -> !line.contains("conhost.exe"))
        .filter(line -> !line.contains("get-video-playlist-content.sh"))
        .filter(line -> !line.contains("exec-script.sh"))
        .filter(line -> !line.trim().endsWith("logout"))
        .map(line -> removeCharactersPastFileExtension(line))
        .toList();
    logger.trace("Playlist content: {}", playlistContent);
    return playlistContent;
  }

  /**
   * Remove the strange characters added after the file extension during the session.
   */
  private static String removeCharactersPastFileExtension(String file) {
    return StringUtils.substring(file, 0, StringUtils.lastIndexOf(file, ".") + 4);
  }
}
