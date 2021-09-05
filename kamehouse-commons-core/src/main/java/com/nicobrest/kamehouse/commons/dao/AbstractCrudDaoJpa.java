package com.nicobrest.kamehouse.commons.dao;

import java.util.List;

/**
 * CRUD operations implementations for all Daos that support CRUD operations.
 *
 * @author nbrest
 */
public abstract class AbstractCrudDaoJpa<E> extends AbstractDaoJpa<E> implements CrudDao<E> {

  /**
   * Get the entity class.
   */
  public abstract Class<E> getEntityClass();

  @Override
  public Long create(E entity) {
    logger.trace("Create {} {}", getEntityClass().getSimpleName(), entity);
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    Long createdId = identifiableEntity.getId();
    logger.trace("Create {} {} response {}", getEntityClass().getSimpleName(), entity, createdId);
    return createdId;
  }

  @Override
  public E read(Long id) {
    logger.trace("Read {} {}", getEntityClass().getSimpleName(), id);
    E entity = findById(getEntityClass(), id);
    logger.trace("Read {} {} response {}", getEntityClass().getSimpleName(), id, entity);
    return entity;
  }

  @Override
  public List<E> readAll() {
    logger.trace("ReadAll {}", getEntityClass().getSimpleName());
    List<E> returnedEntities = findAll(getEntityClass());
    logger.trace("ReadAll {} response {}", getEntityClass().getSimpleName(), returnedEntities);
    return returnedEntities;
  }

  @Override
  public void update(E entity) {
    logger.trace("Update {}", entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    updateEntityInRepository(getEntityClass(), entity, identifiableEntity.getId());
    logger.trace("Update {} completed successfully", entity);
  }

  @Override
  public E delete(Long id) {
    logger.trace("Delete {} {}", getEntityClass().getSimpleName(), id);
    E deletedEntity = deleteEntityFromRepository(getEntityClass(), id);
    logger.trace("Delete {} {} response {}", getEntityClass().getSimpleName(), id, deletedEntity);
    return deletedEntity;
  }
}
