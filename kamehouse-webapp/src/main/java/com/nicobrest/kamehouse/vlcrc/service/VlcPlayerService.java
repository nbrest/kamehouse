package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.commons.service.CrudService;
import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.model.dto.VlcPlayerDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer to manage the registered VLC Players in the application.
 * 
 * @author nbrest
 *
 */
@Service
public class VlcPlayerService extends AbstractCrudService<VlcPlayer, VlcPlayerDto> implements
    CrudService<VlcPlayer, VlcPlayerDto> {

  @Autowired
  @Qualifier("vlcPlayerDaoJpa")
  private VlcPlayerDao vlcPlayerDao;

  public VlcPlayerDao getVlcPlayerDao() {
    return vlcPlayerDao;
  }

  public void setVlcPlayerDao(VlcPlayerDao vlcPlayerDao) {
    this.vlcPlayerDao = vlcPlayerDao;
  }

  @Override
  public Long create(VlcPlayerDto dto) {
    return create(vlcPlayerDao, dto);
  }

  @Override
  public VlcPlayer read(Long id) {
    return read(vlcPlayerDao, id);
  }

  @Override
  public List<VlcPlayer> readAll() {
    return readAll(vlcPlayerDao);
  }

  @Override
  public void update(VlcPlayerDto dto) {
    update(vlcPlayerDao, dto);
  }

  @Override
  public VlcPlayer delete(Long id) {
    return delete(vlcPlayerDao, id);
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

  @Override
  protected VlcPlayer getModel(VlcPlayerDto vlcPlayerDto) {
    VlcPlayer vlcPlayer = new VlcPlayer();
    vlcPlayer.setHostname(vlcPlayerDto.getHostname());
    vlcPlayer.setId(vlcPlayerDto.getId());
    vlcPlayer.setPassword(vlcPlayerDto.getPassword());
    vlcPlayer.setPort(vlcPlayerDto.getPort());
    vlcPlayer.setUsername(vlcPlayerDto.getUsername());
    return vlcPlayer;
  }

  @Override
  protected void validate(VlcPlayer entity) {
    // No validations added yet to VlcPlayer.
  }
}
