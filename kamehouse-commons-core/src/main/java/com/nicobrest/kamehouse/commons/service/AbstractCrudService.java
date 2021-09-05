package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.model.KameHouseEntity;
import com.nicobrest.kamehouse.commons.model.dto.KameHouseDto;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to group CRUD operations on the service layer.
 *
 * @author nbrest
 */
public abstract class AbstractCrudService<E extends KameHouseEntity<D>, D extends KameHouseDto<E>>
    implements CrudService<E, D> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Get crud DAO.
   */
  public abstract CrudDao<E> getCrudDao();

  /**
   * Performs validations on the entity before persisting it to the repository.
   */
  protected abstract void validate(E entity);

  @Override
  public Long create(D dto) {
    logger.trace("Create {}", dto);
    E entity = dto.buildEntity();
    validate(entity);
    Long createdId = getCrudDao().create(entity);
    logger.trace("Create {} response {}", dto, createdId);
    return createdId;
  }

  @Override
  public E read(Long id) {
    logger.trace("Read {}", id);
    E entity = getCrudDao().read(id);
    logger.trace("Read {} response {}", id, entity);
    return entity;
  }

  @Override
  public List<E> readAll() {
    logger.trace("ReadAll");
    List<E> returnedEntities = getCrudDao().readAll();
    logger.trace("ReadAll response {}", returnedEntities);
    return returnedEntities;
  }

  @Override
  public void update(D dto) {
    logger.trace("Update {}", dto);
    E entity = dto.buildEntity();
    validate(entity);
    getCrudDao().update(entity);
    logger.trace("Update {} completed successfully", dto);
  }

  @Override
  public E delete(Long id) {
    logger.trace("Delete {}", id);
    E deletedEntity = getCrudDao().delete(id);
    logger.trace("Delete {} response {}", id, deletedEntity);
    return deletedEntity;
  }
}
