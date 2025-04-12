package com.nicobrest.kamehouse.media.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.media.service.PlaylistService;
import com.nicobrest.kamehouse.media.model.Playlist;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to manage the playlists in the local system.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/media")
public class PlaylistController extends AbstractController {

  private PlaylistService playlistService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public PlaylistController(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  /**
   * Gets all playlists.
   */
  @GetMapping(path = "/playlists")
  public ResponseEntity<List<Playlist>> getAll() {
    List<Playlist> playlists = playlistService.getAll();
    return generateGetResponseEntity(playlists);
  }

  /**
   * Gets a playlist specified as a url parameter.
   */
  @GetMapping(path = "/playlist")
  public ResponseEntity<Playlist> getPlaylist(
      @RequestParam(value = "path", required = true) String path) {
    InputValidator.validateForbiddenCharsForShell(path);
    Playlist playlist = playlistService.getPlaylist(StringUtils.sanitize(path), true);
    return generateGetResponseEntity(playlist);
  }
}
