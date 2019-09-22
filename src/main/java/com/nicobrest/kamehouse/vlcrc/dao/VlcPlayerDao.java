package com.nicobrest.kamehouse.vlcrc.dao;

import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;

import java.util.List;

/**
 * DAO layer to manage registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
public interface VlcPlayerDao {

  /**
   * Creates a VLC player.
   */
  public Long create(VlcPlayer entity);
  
  /**
   * Updates a VLC Player.
   */
  public void update(VlcPlayer entity);
  
  /**
   * Gets a VLC Player by hostname.
   */
  public VlcPlayer getByHostname(String hostname);
  
  /**
   * Deletes a VLC Player.
   */
  public VlcPlayer delete(Long id);
  
  /**
   * Gets all VLC Players registered.
   */
  public List<VlcPlayer> getAll();
}
