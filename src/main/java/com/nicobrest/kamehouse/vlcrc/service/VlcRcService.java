package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.vlcrc.model.VlcRcCommand;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcFileListItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcPlaylistItem;
import com.nicobrest.kamehouse.vlcrc.model.VlcRcStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to interact with the registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
@Service
public class VlcRcService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private VlcPlayerService vlcPlayerService;

  public void setVlcPlayerService(VlcPlayerService vlcPlayerService) {
    this.vlcPlayerService = vlcPlayerService;
  }

  public VlcPlayerService getVlcPlayerService() {
    return vlcPlayerService;
  }

  /**
   * Gets the status information of the specified VLC Player.
   */
  public VlcRcStatus getVlcRcStatus(String hostname) {
    logger.trace("getVlcRcStatus {}", hostname);
    return vlcPlayerService.getByHostname(hostname).getVlcRcStatus();
  }

  /**
   * Executes a command in the specified VLC Player.
   */
  public VlcRcStatus execute(VlcRcCommand vlcRcCommand, String hostname) {
    logger.trace("execute VlcRcCommand {} in {}", vlcRcCommand, hostname);
    VlcRcStatus vlcRcStatus = vlcPlayerService.getByHostname(hostname).execute(vlcRcCommand);
    logger.trace("execute VlcRcCommand {} in {} response {}", vlcRcCommand, hostname, vlcRcStatus);
    return vlcRcStatus;
  }

  /**
   * Gets the current playlist for the selected VLC Player.
   */
  public List<VlcRcPlaylistItem> getPlaylist(String hostname) {
    logger.trace("getPlaylist {}", hostname);
    List<VlcRcPlaylistItem> playlist = vlcPlayerService.getByHostname(hostname).getPlaylist();
    logger.trace("getPlaylist {} response {}", hostname, playlist);
    return playlist;
  }

  /**
   * Browses the file system of the selected VLC Player.
   */
  public List<VlcRcFileListItem> browse(String uri, String hostname) {
    logger.trace("browse {} in {}", uri, hostname);
    List<VlcRcFileListItem> filelist = vlcPlayerService.getByHostname(hostname).browse(uri);
    logger.trace("browse {} in {} response {}", uri, hostname, filelist);
    return filelist;
  }
}
