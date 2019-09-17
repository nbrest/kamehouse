package com.nicobrest.kamehouse.media.video.controller;

import com.nicobrest.kamehouse.main.controller.AbstractController;
import com.nicobrest.kamehouse.media.video.model.Playlist;
import com.nicobrest.kamehouse.media.video.service.VideoPlaylistService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
/**
 * Controller to manage the video playlists in the local system.
 * 
 * @author nbrest
 *
 */
@Controller
@RequestMapping(value = "/api/v1/media/video")
public class VideoPlaylistController extends AbstractController {

  @Autowired
  private VideoPlaylistService videoPlaylistService;

  /**
   * Get all video playlists.
   */
  @GetMapping(path = "/playlists")
  @ResponseBody
  public ResponseEntity<List<Playlist>> getAllVideoPlaylists() {
    logger.trace("In controller /api/v1/media/video/playlists (GET)");
    List<Playlist> videoPlaylists = videoPlaylistService.getAllVideoPlaylists();
    return new ResponseEntity<>(videoPlaylists, HttpStatus.OK);
  }
}
