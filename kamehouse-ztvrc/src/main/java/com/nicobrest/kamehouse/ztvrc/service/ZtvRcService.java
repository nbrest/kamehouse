package com.nicobrest.kamehouse.ztvrc.service;

import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcCommand;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcFileListItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcPlaylistItem;
import com.nicobrest.kamehouse.ztvrc.model.ZtvRcStatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer to interact with the registered ZTV Players in the application.
 *
 * @author nbrest
 */
@Service
public class ZtvRcService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final ZtvPlayerService ztvPlayerService;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public ZtvRcService(ZtvPlayerService ztvPlayerService) {
    this.ztvPlayerService = ztvPlayerService;
  }

  /**
   * Gets the status information of the specified ZTV Player.
   */
  public ZtvRcStatus getZtvRcStatus(String hostname) {
    logger.trace("getZtvRcStatus {}", hostname);
    ZtvPlayer ztvPlayer = ztvPlayerService.getByHostname(hostname);
    return ztvPlayer.getZtvRcStatus();
  }

  /**
   * Executes a command in the specified ZTV Player.
   */
  public ZtvRcStatus execute(ZtvRcCommand ztvRcCommand, String hostname) {
    logger.trace("execute ZtvRcCommand {} in {}", ztvRcCommand, hostname);
    ZtvPlayer ztvPlayer = ztvPlayerService.getByHostname(hostname);
    return ztvPlayer.execute(ztvRcCommand);
  }

  /**
   * Gets the current playlist for the selected ZTV Player.
   */
  public List<ZtvRcPlaylistItem> getPlaylist(String hostname) {
    logger.trace("getPlaylist {}", hostname);
    ZtvPlayer ztvPlayer = ztvPlayerService.getByHostname(hostname);
    return ztvPlayer.getPlaylist();
  }

  /**
   * Browses the file system of the selected ZTV Player.
   */
  public List<ZtvRcFileListItem> browse(String uri, String hostname) {
    logger.trace("browse {} in {}", uri, hostname);
    ZtvPlayer ztvPlayer = ztvPlayerService.getByHostname(hostname);
    return ztvPlayer.browse(uri);
  }
}
