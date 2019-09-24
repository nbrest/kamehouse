package com.nicobrest.kamehouse.vlcrc.service;

import com.nicobrest.kamehouse.main.service.AbstractCrudService;
import com.nicobrest.kamehouse.main.service.CrudService;
import com.nicobrest.kamehouse.vlcrc.dao.VlcPlayerDao;
import com.nicobrest.kamehouse.vlcrc.model.VlcPlayer;
import com.nicobrest.kamehouse.vlcrc.service.dto.VlcPlayerDto;

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
public class VlcPlayerService extends AbstractCrudService implements
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
    return vlcPlayerDao.getByHostname(hostname);
  }

  @Override
  protected <E, D> E getModel(D dto) {
    VlcPlayerDto vlcPlayerDto = (VlcPlayerDto) dto;
    VlcPlayer vlcPlayer = new VlcPlayer();
    vlcPlayer.setHostname(vlcPlayerDto.getHostname());
    vlcPlayer.setId(vlcPlayerDto.getId());
    vlcPlayer.setPassword(vlcPlayerDto.getPassword());
    vlcPlayer.setPort(vlcPlayerDto.getPort());
    vlcPlayer.setUsername(vlcPlayerDto.getUsername());
    return (E) vlcPlayer;
  }

  @Override
  protected <E> void validate(E entity) {
    // No validations added yet to VlcPlayer.
  }
}
