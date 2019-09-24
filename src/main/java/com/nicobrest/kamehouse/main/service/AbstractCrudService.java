package com.nicobrest.kamehouse.main.service;

import com.nicobrest.kamehouse.main.dao.CrudDao;

import java.util.List;

/**
 * Abstract class to group CRUD operations on the service layer.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudService {

  /**
   * Creates an entity in the repository from the DTO.
   */
  protected <E,D> Long create(CrudDao<E> dao, D dto) {
    E entity = getModel(dto);
    validate(entity);
    return dao.create(entity);
  }
  
  /**
   * Reads an entity from the repository for the specified DAO and id.
   */
  protected <E> E read(CrudDao<E> dao, Long id) {
    return dao.read(id);
  }
  
  /**
   * Reads all entities from the repository for the specified DAO.
   */
  protected <E> List<E> readAll(CrudDao<E> dao) {
    return dao.readAll();
  }
  
  /**
   * Updates an entity in the repository from the DTO.
   */
  protected <E, D> void update(CrudDao<E> dao, D dto) {
    E entity = getModel(dto);
    validate(entity);
    dao.update(entity);
  }
 
  /**
   * Deletes an entity from the repository with the specified id.
   */
  protected <E> E delete(CrudDao<E> dao, Long id) {
    return dao.delete(id);
  }
  
  /**
   * Gets the entity model object from the DTO.
   */
  protected abstract <E,D> E getModel(D dto);
  
  /**
   * Performs validations on the entity before persisting it to the repository.
   */
  protected abstract <E> void validate(E entity); 
}