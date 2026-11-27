package com.nicobrest.kamehouse.ztvrc.controller;

import com.nicobrest.kamehouse.commons.controller.AbstractKameHouseCommandController;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommand;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCommandResult;
import com.nicobrest.kamehouse.commons.service.KameHouseCommandService;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcCommand;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcFileListItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import com.nicobrest.kamehouse.ztvrc.model.kamehousecommand.ZtvStatsFullReportKameHouseCommand;
import com.nicobrest.kamehouse.ztvrc.model.kamehousecommand.ZtvStatsKameHouseCommand;
import com.nicobrest.kamehouse.ztvrc.service.ZtvRcService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
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
 * Controller to manage the ztv Players registered in the application.
 *
 * @author nbrest
 */
@RestController
@RequestMapping(value = "/api/v1/ztv-rc/players")
public class ZtvRcController extends AbstractKameHouseCommandController {

  private final ZtvRcService ztvRcService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public ZtvRcController(ZtvRcService ztvRcService,
      KameHouseCommandService kameHouseCommandService) {
    super(kameHouseCommandService);
    this.ztvRcService = ztvRcService;
  }

  /**
   * Gets the status information of the ztv Player passed through the URL.
   */
  @GetMapping(path = "/{hostname}/status")
  public ResponseEntity<ZtvRcStatus> getZtvRcStatus(@PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(hostname);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    ZtvRcStatus ztvRcStatus = ztvRcService.getZtvRcStatus(hostnameSanitized);
    return generateGetResponseEntity(ztvRcStatus, false);
  }

  /**
   * Executes a command in the selected ztv Player.
   */
  @PostMapping(path = "/{hostname}/commands")
  public ResponseEntity<ZtvRcStatus> execCommand(
      @RequestBody ZtvRcCommand ztvRcCommand, @PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(hostname);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    ZtvRcStatus ztvRcStatus = ztvRcService.execute(ztvRcCommand, hostnameSanitized);
    return generatePostResponseEntity(ztvRcStatus, false);
  }

  /**
   * Gets the current playlist from the selected ztv Player.
   */
  @GetMapping(path = "/{hostname}/playlist")
  public ResponseEntity<List<ZtvRcPlaylistItem>> getPlaylist(@PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(hostname);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    List<ZtvRcPlaylistItem> ztvPlaylist = ztvRcService.getPlaylist(hostnameSanitized);
    return generateGetResponseEntity(ztvPlaylist, false);
  }

  /**
   * Browses the ztv Player server's file system.
   */
  @GetMapping(path = "/{hostname}/browse")
  public ResponseEntity<List<ZtvRcFileListItem>> browse(
      @RequestParam(value = "uri", required = false) String uri, @PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(uri);
    InputValidator.validateForbiddenCharsForShell(hostname);
    String uriSanitized = StringUtils.sanitize(uri);
    String hostnameSanitized = StringUtils.sanitize(hostname);
    List<ZtvRcFileListItem> ztvRcFileList = ztvRcService.browse(uriSanitized, hostnameSanitized);
    return generateGetResponseEntity(ztvRcFileList, false);
  }

  /**
   * Get the stats of the ztv player running on the local system.
   */
  @GetMapping(path = "/{hostname}/stats")
  public ResponseEntity<List<KameHouseCommandResult>> stats(
      @RequestParam(value = "fullReport", defaultValue = "false") boolean fullReport,
      @RequestParam(value = "updateStats", defaultValue = "false") boolean updateStats,
      @PathVariable String hostname) {
    InputValidator.validateForbiddenCharsForShell(hostname);
    List<KameHouseCommand> kameHouseCommands = new ArrayList<>();
    if (fullReport) {
      kameHouseCommands.add(new ZtvStatsFullReportKameHouseCommand(updateStats));
    } else {
      kameHouseCommands.add(new ZtvStatsKameHouseCommand(updateStats));
    }
    return execKameHouseCommands(kameHouseCommands);
  }
}
