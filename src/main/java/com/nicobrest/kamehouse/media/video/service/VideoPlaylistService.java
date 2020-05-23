package com.nicobrest.kamehouse.media.video.service;

import com.nicobrest.kamehouse.main.utils.PropertiesUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
@Service
public class VideoPlaylistService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String PROP_PLAYLISTS_PATH_WINDOWS = "playlists.path.windows";
  private static final String PROP_PLAYLISTS_PATH_LINUX = "playlists.path.linux";

  /**
   * Gets all video playlists without their contents.
   */
  public List<Playlist> getAll() {
    return getAll(false);
  }

  /**
   * Gets all video playlists specifying if it should get the contents of each playlist or not.
   */
  public List<Playlist> getAll(boolean fetchContent) {
    logger.trace("getAll");
    Path basePlaylistPath = getBasePlaylistsPath();
    List<Playlist> videoPlaylists = new ArrayList<>();
    try (Stream<Path> filePaths = Files.walk(basePlaylistPath)) {
      Iterator<Path> filePathsIterator = filePaths.iterator();
      while (filePathsIterator.hasNext()) {
        Path playlistPath = filePathsIterator.next();
        if (!playlistPath.toFile().isDirectory()) {
          Playlist playlist = getPlaylist(playlistPath, fetchContent);
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
  public Playlist getPlaylist(String playlistFilename, boolean fetchContent) {
    Path playlistPath = Paths.get(playlistFilename);
    return getPlaylist(playlistPath, fetchContent);
  }

  /**
   * Get the specified playlist.
   */
  public Playlist getPlaylist(Path playlistPath, boolean fetchContent) {
    logger.trace("Get playlist {}", playlistPath.toString());
    if (!Files.exists(playlistPath)) {
      logger.warn("File {} doesn't exist.", playlistPath.toString());
      return null;
    }
    //TODO: ADD FILTERS TO ONLY GET PLAYLISTS ON BASEPATH, CONSIDER USERS USING ../ TO GET OUT OF IT
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
    return playlist;
  }

  /**
   * Get the base path where to look for playlists.
   */
  private Path getBasePlaylistsPath() {
    String userHome = PropertiesUtils.getUserHome();
    String videoPlaylistsHome;
    if (PropertiesUtils.isWindowsHost()) {
      String playlistsPathWindows = PropertiesUtils.getMediaVideoProperty(
          PROP_PLAYLISTS_PATH_WINDOWS);
      videoPlaylistsHome = userHome + playlistsPathWindows;
    } else {
      String playlistsPathLinux = PropertiesUtils.getMediaVideoProperty(
          PROP_PLAYLISTS_PATH_LINUX);
      videoPlaylistsHome = userHome + playlistsPathLinux;
    }
    return Paths.get(videoPlaylistsHome);
  }

  /**
   * Gets the category of the playlist based on the base path.
   */
  //TODO: FIX THIS LOGIC. IT'S BROKEN IN THE UNIT TESTS, SHOWS 'oes\\dc' AS CATEGORY
  private String getCategory(Path basePath, Path filePath) {
    int basePathLength = basePath.toFile().getAbsolutePath().length();
    Path parentPath = filePath.getParent();
    if (parentPath != null) {
      String absoluteParentFilePath = parentPath.toFile().getAbsolutePath(); 
      return absoluteParentFilePath.substring(basePathLength + 1);
    } else {
      return null;
    }
  }

  /**
   * Get the files in the playlist.
   */
  private List<String> getPlaylistContent(String playlistPath) {
    List<String> files = null;
    logger.trace("Getting content for playlist {}", playlistPath);
    try {
      files = Files.readAllLines(Paths.get(playlistPath))
          .stream()
          // Remove comments of m3u files
          .filter(file -> !file.startsWith("#"))
          // Remove empty lines
          .filter(file -> !file.trim().isEmpty())
          .collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error reading {} content", playlistPath, e);
    }
    return files;
  }
}
