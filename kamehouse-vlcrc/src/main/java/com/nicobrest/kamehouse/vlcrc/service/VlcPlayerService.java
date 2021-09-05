package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the registered VLC Players in the application.
 *
 * @author nbrest
 */
@Service
public class VlcPlayerService extends AbstractCrudService<VlcPlayer, VlcPlayerDto> {

  @Autowired
  @Qualifier("vlcPlayerDaoJpa")
  private VlcPlayerDao vlcPlayerDao;

  @Override
  public CrudDao<VlcPlayer> getCrudDao() {
    return vlcPlayerDao;
  }

  @Override
  protected void validate(VlcPlayer entity) {
    // No validations added yet to VlcPlayer.
  }

  /**
   * Gets a VLC Player by hostname.
   */
  public VlcPlayer getByHostname(String hostname) {
    logger.trace("getByHostname {}", hostname);
    VlcPlayer vlcPlayer = vlcPlayerDao.getByHostname(hostname);
    logger.trace("getByHostname {} response {}", hostname, vlcPlayer);
    return vlcPlayer;
  }
}
