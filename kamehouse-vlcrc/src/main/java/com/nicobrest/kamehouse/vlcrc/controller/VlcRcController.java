package com.nicobrest.kamehouse.vlcrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractController;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import com.nicobrest.kamehouse.vlcrc.service.VlcRcService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to manage the VLC Players registered in the application.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/vlc-rc/players")
public class VlcRcController extends AbstractController {

  private VlcRcService vlcRcService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public VlcRcController(VlcRcService vlcRcService) {
    this.vlcRcService = vlcRcService;
  }

  /**
   * Gets the status information of the VLC Player passed through the URL.
   */
  @GetMapping(path = "/{hostname}/status")
  public ResponseEntity<VlcRcStatus> getVlcRcStatus(@PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(hostname);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    VlcRcStatus vlcRcStatus = vlcRcService.getVlcRcStatus(hostnameSanitized);
    return generateGetResponseEntity(vlcRcStatus, false);
  }

  /**
   * Executes a command in the selected VLC Player.
   */
  @PostMapping(path = "/{hostname}/commands")
  public ResponseEntity<VlcRcStatus> execCommand(
      @RequestBody VlcRcCommand vlcRcCommand, @PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(hostname);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    VlcRcStatus vlcRcStatus = vlcRcService.execute(vlcRcCommand, hostnameSanitized);
    return generatePostResponseEntity(vlcRcStatus, false);
  }

  /**
   * Gets the current playlist from the selected VLC Player.
   */
  @GetMapping(path = "/{hostname}/playlist")
  public ResponseEntity<List<VlcRcPlaylistItem>> getPlaylist(@PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(hostname);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    List<VlcRcPlaylistItem> vlcPlaylist = vlcRcService.getPlaylist(hostnameSanitized);
    return generateGetResponseEntity(vlcPlaylist, false);
  }

  /**
   * Browses the VLC Player server's file system.
   */
  @GetMapping(path = "/{hostname}/browse")
  public ResponseEntity<List<VlcRcFileListItem>> browse(
      @RequestParam(value = "uri", required = false) String uri, @PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(uri);
    InputValidator.validateForbiddenCharsForShell(hostname);
    String uriSanitized = StringUtils.sanitize(uri);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    List<VlcRcFileListItem> vlcRcFileList = vlcRcService.browse(uriSanitized, hostnameSanitized);
    return generateGetResponseEntity(vlcRcFileList, false);
  }
}
