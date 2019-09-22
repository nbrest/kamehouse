package com.nicobrest.kamehouse.media.video.service;

import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.model.PlaylistComparator;
import com.nicobrest.kamehouse.utils.PropertiesUtils;

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
   * Get all video playlists.
   */
  public List<Playlist> getAll() {
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
    List<Playlist> videoPlaylists = new ArrayList<>();
    Path basePath = Paths.get(videoPlaylistsHome);
    try (Stream<Path> filePaths = Files.walk(basePath)) {
      Iterator<Path> filePathsIterator = filePaths.iterator();
      while (filePathsIterator.hasNext()) {
        Path filePath = filePathsIterator.next();
        if (!filePath.toFile().isDirectory()) {
          Path fileName = filePath.getFileName();
          if (fileName != null) {
            Playlist playlist = new Playlist();
            playlist.setName(fileName.toString());
            String category = getCategory(basePath, filePath);
            playlist.setCategory(category);
            playlist.setPath(filePath.toString());
            videoPlaylists.add(playlist);
          }
        }
      }
    } catch (IOException e) {
      logger.error("An exception occurred while getting all the video playlists. Message: {}", e
          .getMessage());
    }
    videoPlaylists.sort(new PlaylistComparator());
    return videoPlaylists;
  }

  /**
   * Get the category of the playlist based on the base path.
   */
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
}
