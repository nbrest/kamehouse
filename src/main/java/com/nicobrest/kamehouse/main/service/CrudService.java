package com.nicobrest.kamehouse.main.service;

/**
 * CRUD interface to be implemented by all services that support CRUD operations.
 * 
 * @author nbrest
 */
public interface CrudService<E, D> {

  /**
   * Creates a new Entity in the repository from it's DTO.
   */
  Long create(D dto);
}
