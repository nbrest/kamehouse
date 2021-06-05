package com.nicobrest.kamehouse.media.video.service;

import com.nicobrest.kamehouse.main.utils.PropertiesUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service to manage the video playlists in the local system.
 *
 * @author nbrest
 *
 */
public class VideoPlaylistService {

  public static final String PROP_MEDIA_SERVER_NAME = "media.server.name";
  public static final String PROP_PLAYLISTS_PATH_LINUX = "playlists.path.linux";
  public static final String PROP_PLAYLISTS_PATH_REMOTE_LAN_SHARE = "playlists.path.remote.lan"
      + ".share";
  public static final String PROP_PLAYLISTS_PATH_REMOTE_HTTP = "playlists.path.remote.http";
  public static final String PROP_PLAYLISTS_PATH_WINDOWS = "playlists.path.windows";
  private static final String REMOTE_SERVER = "[REMOTE_SERVER]";
  private static final String SUPPORTED_PLAYLIST_EXTENSION = ".m3u";
  private static final String VIDEO_PLAYLIST_CACHE = "videoPlaylist";
  private static final String VIDEO_PLAYLISTS_CACHE = "videoPlaylists";
  private static final String ANIME = "anime";
  private static final String CARTOONS = "cartoons";

  private final Logger logger = LoggerFactory.getLogger(getClass());

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
   * Get the specified playlist.
   */
  @Cacheable(value = VIDEO_PLAYLIST_CACHE)
  public Playlist getPlaylist(String playlistFilename, boolean fetchContent) {
    logger.trace("Get playlist {}", playlistFilename);
    Path playlistPath = Paths.get(playlistFilename);
    if (!isValidPlaylist(playlistPath)) {
      logger.error("Invalid playlist path specified. Check the validations for supported "
          + "playlists");
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
   * Get the base path where to look for playlists.
   */
  private Path getBasePlaylistsPath() {
    String userHome = PropertiesUtils.getUserHome();
    String videoPlaylistsHome;
    if (isMediaServerLocalhost()) {
      if (PropertiesUtils.isWindowsHost()) {
        String playlistsPathWindows = PropertiesUtils.getMediaVideoProperty(
            PROP_PLAYLISTS_PATH_WINDOWS);
        videoPlaylistsHome = userHome + playlistsPathWindows;
      } else {
        String playlistsPathLinux = PropertiesUtils.getMediaVideoProperty(
            PROP_PLAYLISTS_PATH_LINUX);
        videoPlaylistsHome = userHome + playlistsPathLinux;
      }
    } else {
      String mediaServer = PropertiesUtils.getMediaVideoProperty(PROP_MEDIA_SERVER_NAME);
      String playlistsPathRemote = null;
      if (PropertiesUtils.isWindowsHost()) {
        playlistsPathRemote = PropertiesUtils.getMediaVideoProperty(
            PROP_PLAYLISTS_PATH_REMOTE_LAN_SHARE);
      } else {
        playlistsPathRemote = PropertiesUtils.getMediaVideoProperty(
            PROP_PLAYLISTS_PATH_REMOTE_HTTP);
      }
      playlistsPathRemote = playlistsPathRemote.replace(REMOTE_SERVER, mediaServer);
      videoPlaylistsHome = userHome + playlistsPathRemote;
    }
    return Paths.get(videoPlaylistsHome);
  }

  /**
   * Checks if the current server running kamehouse is the media server.
   */
  private static boolean isMediaServerLocalhost() {
    String mediaServer = PropertiesUtils.getMediaVideoProperty(PROP_MEDIA_SERVER_NAME);
    return mediaServer.equalsIgnoreCase(PropertiesUtils.getHostname());
  }

  /**
   * Gets the category of the playlist based on the base path.
   */
  private String getCategory(Path basePath, Path filePath) {
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
   * Clean up path from unnecessary jumps such as '/./' or '\.\'.
   */
  private String sanitizePath(String path) {
    return path.replaceAll("\\\\.\\\\", "\\\\")
        .replaceAll("/./","/");
  }

  /**
   * Validate that the playlist specified is valid.
   */
  private boolean isValidPlaylist(Path playlistPath) {
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
  private List<String> getPlaylistContent(String playlistFilename) {
    List<String> files = null;
    logger.trace("Getting content for playlist {}", playlistFilename);
    try {
      files = Files.readAllLines(Paths.get(playlistFilename))
          .stream()
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
}
