package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractCrudController;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import com.nicobrest.kamehouse.vlcrc.service.VlcPlayerService;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping(value = "/api/v1/vlc-rc")
public class VlcRcController extends AbstractCrudController<VlcPlayer, VlcPlayerDto> {

  @Autowired
  private VlcRcService vlcRcService;

  @Autowired
  private VlcPlayerService vlcPlayerService;

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CrudService<VlcPlayer, VlcPlayerDto> getCrudService() {
    return vlcPlayerService;
  }

  /**
   * Creates a VLC Player.
   */
  @PostMapping(path = "/players")
  @ResponseBody
  public ResponseEntity<Long> create(@RequestBody VlcPlayerDto dto) {
    return super.create(dto);
  }

  /**
   * Reads a VLC Player by it's id.
   */
  @GetMapping(path = "/players/{id}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> read(@PathVariable Long id) {
    return super.read(id);
  }

  /**
   * Reads all VLC Players registered in the application.
   */
  @GetMapping(path = "/players")
  @ResponseBody
  public ResponseEntity<List<VlcPlayer>> readAll() {
    return super.readAll();
  }

  /**
   * Updates the VLC Player passed as a URL parameter.
   */
  @PutMapping(path = "/players/{id}")
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody VlcPlayerDto dto) {
    return super.update(id, dto);
  }

  /**
   * Deletes the VLC Player passed as a URL parameter.
   */
  @DeleteMapping(path = "/players/{id}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> delete(@PathVariable Long id) {
    return super.delete(id);
  }

  /**
   * Gets the VLC Player passed as a URL parameter.
   */
  @GetMapping(path = "/players/hostname/{hostname}")
  @ResponseBody
  public ResponseEntity<VlcPlayer> getByHostname(@PathVariable String hostname) {
    VlcPlayer vlcPlayer = vlcPlayerService.getByHostname(hostname);
    return generateGetResponseEntity(vlcPlayer);
  }

  /**
   * Gets the status information of the VLC Player passed through the URL.
   */
  @GetMapping(path = "/players/{hostname}/status")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> getVlcRcStatus(@PathVariable String hostname) {
    VlcRcStatus vlcRcStatus = vlcRcService.getVlcRcStatus(hostname);
    return generateGetResponseEntity(vlcRcStatus, false);
  }

  /**
   * Executes a command in the selected VLC Player.
   */
  @PostMapping(path = "/players/{hostname}/commands")
  @ResponseBody
  public ResponseEntity<VlcRcStatus> execCommand(
      @RequestBody VlcRcCommand vlcRcCommand, @PathVariable String hostname) {
    VlcRcStatus vlcRcStatus = vlcRcService.execute(vlcRcCommand, hostname);
    return generatePostResponseEntity(vlcRcStatus, false);
  }

  /**
   * Gets the current playlist from the selected VLC Player.
   */
  @GetMapping(path = "/players/{hostname}/playlist")
  @ResponseBody
  public ResponseEntity<List<VlcRcPlaylistItem>> getPlaylist(@PathVariable String hostname) {
    List<VlcRcPlaylistItem> vlcPlaylist = vlcRcService.getPlaylist(hostname);
    return generateGetResponseEntity(vlcPlaylist, false);
  }

  /**
   * Browses the VLC Player server's file system.
   */
  @GetMapping(path = "/players/{hostname}/browse")
  @ResponseBody
  public ResponseEntity<List<VlcRcFileListItem>> browse(
      @RequestParam(value = "uri", required = false) String uri, @PathVariable String hostname) {
    List<VlcRcFileListItem> vlcRcFileList = vlcRcService.browse(uri, hostname);
    return generateGetResponseEntity(vlcRcFileList, false);
  }
}
