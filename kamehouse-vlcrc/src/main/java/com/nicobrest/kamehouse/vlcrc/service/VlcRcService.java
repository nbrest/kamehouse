package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer to interact with the registered VLC Players in the application.
 *
 * @author nbrest
 */
@Service
public class VlcRcService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired private VlcPlayerService vlcPlayerService;

  public void setVlcPlayerService(VlcPlayerService vlcPlayerService) {
    this.vlcPlayerService = vlcPlayerService;
  }

  public VlcPlayerService getVlcPlayerService() {
    return vlcPlayerService;
  }

  /** Gets the status information of the specified VLC Player. */
  public VlcRcStatus getVlcRcStatus(String hostname) {
    logger.trace("getVlcRcStatus {}", hostname);
    return vlcPlayerService.getByHostname(hostname).getVlcRcStatus();
  }

  /** Executes a command in the specified VLC Player. */
  public VlcRcStatus execute(VlcRcCommand vlcRcCommand, String hostname) {
    logger.trace("execute VlcRcCommand {} in {}", vlcRcCommand, hostname);
    return vlcPlayerService.getByHostname(hostname).execute(vlcRcCommand);
  }

  /** Gets the current playlist for the selected VLC Player. */
  public List<VlcRcPlaylistItem> getPlaylist(String hostname) {
    logger.trace("getPlaylist {}", hostname);
    return vlcPlayerService.getByHostname(hostname).getPlaylist();
  }

  /** Browses the file system of the selected VLC Player. */
  public List<VlcRcFileListItem> browse(String uri, String hostname) {
    logger.trace("browse {} in {}", uri, hostname);
    return vlcPlayerService.getByHostname(hostname).browse(uri);
  }
}
