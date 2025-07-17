package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.model.KameHouseDtoTranslator;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to group CRUD operations on the service layer.
 *
 * @author nbrest
 */
public abstract class AbstractCrudService<E extends Identifiable, D extends Identifiable> implements
    CrudService<E, D> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Get crud DAO.
   */
  public abstract CrudDao<E> getCrudDao();

  /**
   * Get dto translator.
   */
  public abstract KameHouseDtoTranslator<E, D> getDtoTranslator();

  /**
   * Performs validations on the entity before persisting it to the repository.
   */
  protected abstract void validate(E entity);

  @Override
  public Long create(D dto) {
    if (logger.isTraceEnabled()) {
      logger.trace("Create {}", StringUtils.sanitize(dto));
    }
    E entity = getDtoTranslator().buildEntity(dto);
    validate(entity);
    Long createdId = getCrudDao().create(entity);
    if (logger.isTraceEnabled()) {
      logger.trace("Create {} response {}", StringUtils.sanitize(dto), createdId);
    }
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
    return readAll(0, null, true);
  }

  @Override
  public List<E> readAll(Integer maxRows, String sortColumn, Boolean sortAscending) {
    logger.trace("Read all maxRows: {}, sortColumn: {}, sortAscending: {}", maxRows,
        sortColumn, sortAscending);
    List<E> returnedEntities = getCrudDao().readAll(maxRows, sortColumn, sortAscending);
    logger.trace("ReadAll response {}", returnedEntities);
    return returnedEntities;
  }

  @Override
  public void update(D dto) {
    if (logger.isTraceEnabled()) {
      logger.trace("Update {}", StringUtils.sanitize(dto));
    }
    E entity = getDtoTranslator().buildEntity(dto);
    validate(entity);
    getCrudDao().update(entity);
    if (logger.isTraceEnabled()) {
      logger.trace("Update {} completed successfully", StringUtils.sanitize(dto));
    }
  }

  @Override
  public E delete(Long id) {
    logger.trace("Delete {}", id);
    E deletedEntity = getCrudDao().delete(id);
    logger.trace("Delete {} response {}", id, deletedEntity);
    return deletedEntity;
  }
}
