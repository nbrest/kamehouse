package com.nicobrest.kamehouse.media.video.service;

import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.model.PlaylistComparator;
import com.nicobrest.kamehouse.utils.SystemPropertiesUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class VideoPlaylistService {

  private static final Logger logger = LoggerFactory.getLogger(VideoPlaylistService.class);

  /**
   * Get all video playlists.
   */
  public List<Playlist> getAllVideoPlaylists() {
    String userHome = SystemPropertiesUtils.getUserHome();
    String videoPlaylistsHome;
    if (SystemPropertiesUtils.IS_WINDOWS_HOST) {
      videoPlaylistsHome = userHome + "\\git\\texts\\video_playlists\\windows\\niko4tbusb";
    } else {
      videoPlaylistsHome = userHome + "/git/texts/video_playlists/linux/niko4tbusb";
    }
    List<Playlist> videoPlaylists = new ArrayList<Playlist>();
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
            // TODO set a flag to determine if I am requested to list the files
            // in the playlist
            // and if I do, read the playlist file, and add the contained files
            // to the
            // Playlist.files attribute
            videoPlaylists.add(playlist);
          }
        }
      }
    } catch (IOException e) {
      logger.error("An exception occurred while getting all the video playlists. Message: " + e
          .getMessage());
      e.printStackTrace();
    }
    videoPlaylists.sort(new PlaylistComparator());
    return videoPlaylists;
  }

  public String getCategory(Path basePath, Path filePath) {
    int basePathLength = basePath.toFile().getAbsolutePath().length();
    Path parentPath = filePath.getParent();
    if (parentPath != null) {
      String absoluteParentFilePath = parentPath.toFile().getAbsolutePath();
      String relativeFilePath = absoluteParentFilePath.substring(basePathLength + 1);
      return relativeFilePath + File.separator;
    } else {
      return null;
    }
  }
}
