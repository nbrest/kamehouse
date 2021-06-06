package com.nicobrest.kamehouse.commons.service;

import java.util.List;

/**
 * CRUD interface to be implemented by all services that support CRUD
 * operations.
 * 
 * @author nbrest
 */
public interface CrudService<E, D> {

  /**
   * Creates a new Entity in the repository from it's DTO.
   */
  Long create(D dto);

  /**
   * Reads an entity from the repository by its id.
   */
  public E read(Long id);

  /**
   * Reads all the entities from the repository of type T.
   */
  public List<E> readAll();

  /**
   * Updates an entity on the repository from it's DTO.
   */
  public void update(D dto);

  /**
   * Deletes an entity from the repository.
   */
  public E delete(Long id);
}
