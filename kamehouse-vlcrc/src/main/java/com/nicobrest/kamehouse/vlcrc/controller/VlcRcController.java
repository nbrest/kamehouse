package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to manage the VLC Players registered in the application.
 *
 * @author nbrest
 */
@Controller
@RequestMapping(value = "/api/v1/vlc-rc/players")
public class VlcRcController extends AbstractController {

  @Autowired
  private VlcRcService vlcRcService;

  /**
   * Gets the status information of the VLC Player passed through the URL.
   */
  @GetMapping(path = "/{hostname}/status")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> getVlcRcStatus(@PathVariable String hostname) {
    VlcRcStatus vlcRcStatus = vlcRcService.getVlcRcStatus(hostname);
    return generateGetResponseEntity(vlcRcStatus, false);
  }

  /**
   * Executes a command in the selected VLC Player.
   */
  @PostMapping(path = "/{hostname}/commands")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> execCommand(
      @RequestBody VlcRcCommand vlcRcCommand, @PathVariable String hostname) {
    VlcRcStatus vlcRcStatus = vlcRcService.execute(vlcRcCommand, hostname);
    return generatePostResponseEntity(vlcRcStatus, false);
  }

  /**
   * Gets the current playlist from the selected VLC Player.
   */
  @GetMapping(path = "/{hostname}/playlist")
  @ResponseBody
  public ResponseEntity<List<VlcRcPlaylistItem>> getPlaylist(@PathVariable String hostname) {
    List<VlcRcPlaylistItem> vlcPlaylist = vlcRcService.getPlaylist(hostname);
    return generateGetResponseEntity(vlcPlaylist, false);
  }

  /**
   * Browses the VLC Player server's file system.
   */
  @GetMapping(path = "/{hostname}/browse")
  @ResponseBody
  public ResponseEntity<List<VlcRcFileListItem>> browse(
      @RequestParam(value = "uri", required = false) String uri, @PathVariable String hostname) {
    List<VlcRcFileListItem> vlcRcFileList = vlcRcService.browse(uri, hostname);
    return generateGetResponseEntity(vlcRcFileList, false);
  }
}
