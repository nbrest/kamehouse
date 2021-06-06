package com.nicobrest.kamehouse.commons.dao;

import java.util.List;

/**
 * Generic interface that all DAOs that support CRUD operations should
 * implement.
 * 
 * @author nbrest
 */
public interface CrudDao<T> {

  /**
   * Creates an entity in the repository. Returns the ID of the newly created
   * entity.
   */
  public Long create(T entity);

  /**
   * Reads an entity from the repository by its id.
   */
  public T read(Long id);

  /**
   * Reads all the entities from the repository of type T.
   */
  public List<T> readAll();
  
  /**
   * Updates an entity on the repository.
   */
  public void update(T entity);

  /**
   * Deletes an entity from the repository.
   */
  public T delete(Long id);
}
