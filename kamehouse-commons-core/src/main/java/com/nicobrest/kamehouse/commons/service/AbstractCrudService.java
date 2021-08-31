package com.nicobrest.kamehouse.commons.service;

import com.nicobrest.kamehouse.commons.dao.CrudDao;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to group CRUD operations on the service layer.
 *
 * @author nbrest
 */
public abstract class AbstractCrudService<E, D> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Creates an entity in the repository from the DTO. */
  protected Long create(CrudDao<E> dao, D dto) {
    logger.trace("Create {}", dto);
    E entity = getModel(dto);
    validate(entity);
    Long createdId = dao.create(entity);
    logger.trace("Create {} response {}", dto, createdId);
    return createdId;
  }

  /** Reads an entity from the repository for the specified DAO and id. */
  protected E read(CrudDao<E> dao, Long id) {
    logger.trace("Read {}", id);
    E entity = dao.read(id);
    logger.trace("Read {} response {}", id, entity);
    return entity;
  }

  /** Reads all entities from the repository for the specified DAO. */
  protected List<E> readAll(CrudDao<E> dao) {
    logger.trace("ReadAll");
    List<E> returnedEntities = dao.readAll();
    logger.trace("ReadAll response {}", returnedEntities);
    return returnedEntities;
  }

  /** Updates an entity in the repository from the DTO. */
  protected void update(CrudDao<E> dao, D dto) {
    logger.trace("Update {}", dto);
    E entity = getModel(dto);
    validate(entity);
    dao.update(entity);
    logger.trace("Update {} completed successfully", dto);
  }

  /** Deletes an entity from the repository with the specified id. */
  protected E delete(CrudDao<E> dao, Long id) {
    logger.trace("Delete {}", id);
    E deletedEntity = dao.delete(id);
    logger.trace("Delete {} response {}", id, deletedEntity);
    return deletedEntity;
  }

  /** Gets the entity model object from the DTO. */
  protected abstract E getModel(D dto);

  /** Performs validations on the entity before persisting it to the repository. */
  protected abstract void validate(E entity);
}
