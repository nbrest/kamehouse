package com.nicobrest.kamehouse.main.dao;

import java.util.List;

/**
 * CRUD operations implementations for all Daos that support CRUD operations.
 * 
 * @author nicolas.brest
 *
 */
public abstract class AbstractCrudDaoJpa extends AbstractDaoJpa {

  /**
   * Creates an entity of the specified type in the repository.
   */
  public <T> Long create(Class<T> clazz, T entity) {
    logger.trace("Create {}", clazz.getSimpleName());
    persistEntityInRepository(entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    return identifiableEntity.getId();
  }

  /**
   * Reads an entity of the specified type from the repository.
   */
  public <T> T read(Class<T> clazz, Long id) {
    logger.trace("Read {}", clazz.getSimpleName());
    return findById(clazz, id);
  }
  
  /**
   * Reads all entities of the specified type from the repository.
   */
  public <T> List<T> readAll(Class<T> clazz) {
    logger.trace("Read all  {}", clazz.getSimpleName());
    return findAll(clazz);
  }
  
  /**
   * Updates an entity of the specified type in the repository.
   */
  public <T> void update(Class<T> clazz, T entity) {
    logger.trace("Update {}", entity);
    Identifiable identifiableEntity = (Identifiable) entity;
    updateEntityInRepository(clazz, entity, identifiableEntity.getId());
  }

  /**
   * Deletes an entity of the specified type from the repository.
   */
  public <T> T delete(Class<T> clazz, Long id) {
    logger.trace("Delete {}", clazz.getSimpleName());
    return deleteEntityFromRepository(clazz, id);
  }
}
