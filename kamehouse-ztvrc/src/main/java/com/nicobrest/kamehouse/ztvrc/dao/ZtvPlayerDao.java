package com.nicobrest.kamehouse.ztvrc.dao;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;

/**
 * DAO layer to manage registered ZTV Players in the application.
 *
 * @author nbrest
 */
public interface ZtvPlayerDao extends CrudDao<ZtvPlayer> {

  /** Gets a ZTV Player by hostname. */
  public ZtvPlayer getByHostname(String hostname);
}
