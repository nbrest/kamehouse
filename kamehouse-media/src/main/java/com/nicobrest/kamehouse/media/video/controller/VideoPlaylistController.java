package com.nicobrest.kamehouse.media.video.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.service.VideoPlaylistService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to manage the video playlists in the local system.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/media/video")
public class VideoPlaylistController extends AbstractController {

  @Autowired
  private VideoPlaylistService videoPlaylistService;

  /**
   * Gets all video playlists.
   */
  @GetMapping(path = "/playlists")
  @ResponseBody
  public ResponseEntity<List<Playlist>> getAll() {
    List<Playlist> videoPlaylists = videoPlaylistService.getAll();
    return generateGetResponseEntity(videoPlaylists);
  }

  /**
   * Gets a video playlist specified as a url parameter.
   */
  @GetMapping(path = "/playlist")
  @ResponseBody
  public ResponseEntity<Playlist> getPlaylist(
      @RequestParam(value = "path", required = true) String path) {
    Playlist playlist = videoPlaylistService.getPlaylist(StringUtils.sanitizeInput(path), true);
    return generateGetResponseEntity(playlist);
  }
}
