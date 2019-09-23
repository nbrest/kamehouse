package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.main.dao.CrudDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

/**
 * DAO layer to manage registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
public interface VlcPlayerDao extends CrudDao<VlcPlayer> {

  /**
   * Gets a VLC Player by hostname.
   */
  public VlcPlayer getByHostname(String hostname);
}
