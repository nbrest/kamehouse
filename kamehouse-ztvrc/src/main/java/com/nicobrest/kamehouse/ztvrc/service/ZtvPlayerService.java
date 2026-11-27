package com.nicobrest.kamehouse.ztvrc.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.service.AbstractCrudService;
import com.nicobrest.kamehouse.ztvrc.dao.ZtvPlayerDao;
import com.nicobrest.kamehouse.ztvrc.model.ZtvPlayer;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDto;
import com.nicobrest.kamehouse.ztvrc.model.dto.ZtvPlayerDtoTranslator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service layer to manage the registered ZTV Players in the application.
 *
 * @author nbrest
 */
@Service
public class ZtvPlayerService extends AbstractCrudService<ZtvPlayer, ZtvPlayerDto> {

  private static final ZtvPlayerDtoTranslator TRANSLATOR = new ZtvPlayerDtoTranslator();
  private final ZtvPlayerDao ztvPlayerDao;

  @Autowired
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public ZtvPlayerService(@Qualifier("ztvPlayerDaoJpa") ZtvPlayerDao ztvPlayerDao) {
    this.ztvPlayerDao = ztvPlayerDao;
  }

  @Override
  public CrudDao<ZtvPlayer> getCrudDao() {
    return ztvPlayerDao;
  }

  @Override
  public KameHouseDtoTranslator<ZtvPlayer, ZtvPlayerDto> getDtoTranslator() {
    return TRANSLATOR;
  }

  @Override
  protected void validate(ZtvPlayer entity) {
    // No validations added yet to ZTV Player.
  }

  /**
   * Gets a ZTV Player by hostname.
   */
  public ZtvPlayer getByHostname(String hostname) {
    logger.trace("getByHostname {}", hostname);
    ZtvPlayer ztvPlayer = ztvPlayerDao.getByHostname(hostname);
    logger.trace("getByHostname {} response {}", hostname, ztvPlayer);
    return ztvPlayer;
  }
}
