package com.nicobrest.kamehouse.commons.dao;

import java.util.List;

/**
 * CRUD operations implementations for all Daos that support CRUD operations.
 *
 * @author nbrest
 */
public abstract class AbstractCrudDaoJpa extends AbstractDaoJpa {

  /** Creates an entity of the specified type in the repository. */
  public <T> Long create(Class<T> clazz, T entity) {
    logger.trace("Create {} {}", clazz.getSimpleName(), entity);
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    Long createdId = identifiableEntity.getId();
    logger.trace("Create {} {} response {}", clazz.getSimpleName(), entity, createdId);
    return createdId;
  }

  /** Reads an entity of the specified type from the repository. */
  public <T> T read(Class<T> clazz, Long id) {
    logger.trace("Read {} {}", clazz.getSimpleName(), id);
    T entity = findById(clazz, id);
    logger.trace("Read {} {} response {}", clazz.getSimpleName(), id, entity);
    return entity;
  }

  /** Reads all entities of the specified type from the repository. */
  public <T> List<T> readAll(Class<T> clazz) {
    logger.trace("ReadAll {}", clazz.getSimpleName());
    List<T> returnedEntities = findAll(clazz);
    logger.trace("ReadAll {} response {}", clazz.getSimpleName(), returnedEntities);
    return returnedEntities;
  }

  /** Updates an entity of the specified type in the repository. */
  public <T> void update(Class<T> clazz, T entity) {
    logger.trace("Update {}", entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    updateEntityInRepository(clazz, entity, identifiableEntity.getId());
    logger.trace("Update {} completed successfully", entity);
  }

  /** Deletes an entity of the specified type from the repository. */
  public <T> T delete(Class<T> clazz, Long id) {
    logger.trace("Delete {} {}", clazz.getSimpleName(), id);
    T deletedEntity = deleteEntityFromRepository(clazz, id);
    logger.trace("Delete {} {} response {}", clazz.getSimpleName(), id, deletedEntity);
    return deletedEntity;
  }
}
