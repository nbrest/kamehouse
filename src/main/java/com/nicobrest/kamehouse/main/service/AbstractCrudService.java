package com.nicobrest.kamehouse.main.service;

import com.nicobrest.kamehouse.main.dao.CrudDao;

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
   * Gets the entity model object from the DTO.
   */
  protected abstract <E,D> E getModel(D dto);
  
  /**
   * Performs validations on the entity before persisting it to the repository.
   */
  protected abstract <E> void validate(E entity); 
}