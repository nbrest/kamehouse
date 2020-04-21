package com.nicobrest.kamehouse.main.service;

import com.nicobrest.kamehouse.main.dao.CrudDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Abstract class to group CRUD operations on the service layer.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudService<E, D> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Creates an entity in the repository from the DTO.
   */
  protected Long create(CrudDao<E> dao, D dto) {
    logger.trace("Create {}", dto.toString());
    E entity = getModel(dto);
    validate(entity);
    return dao.create(entity);
  }

  /**
   * Reads an entity from the repository for the specified DAO and id.
   */
  protected E read(CrudDao<E> dao, Long id) {
    logger.trace("Read {}", id);
    return dao.read(id);
  }

  /**
   * Reads all entities from the repository for the specified DAO.
   */
  protected List<E> readAll(CrudDao<E> dao) {
    logger.trace("ReadAll");
    return dao.readAll();
  }

  /**
   * Updates an entity in the repository from the DTO.
   */
  protected void update(CrudDao<E> dao, D dto) {
    logger.trace("Update {}", dto.toString());
    E entity = getModel(dto);
    validate(entity);
    dao.update(entity);
  }

  /**
   * Deletes an entity from the repository with the specified id.
   */
  protected E delete(CrudDao<E> dao, Long id) {
    logger.trace("Delete {}", id);
    return dao.delete(id);
  }

  /**
   * Gets the entity model object from the DTO.
   */
  protected abstract E getModel(D dto);

  /**
   * Performs validations on the entity before persisting it to the repository.
   */
  protected abstract void validate(E entity);
}