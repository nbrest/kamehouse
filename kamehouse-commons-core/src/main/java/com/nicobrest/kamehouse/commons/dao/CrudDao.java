package com.nicobrest.kamehouse.commons.dao;

import java.util.List;

/**
 * Generic interface that all DAOs that support CRUD operations should implement.
 *
 * @author nbrest
 */
public interface CrudDao<E> {

  /** Creates an entity in the repository. Returns the ID of the newly created entity. */
  public Long create(E entity);

  /** Reads an entity from the repository by its id. */
  public E read(Long id);

  /** Reads all the entities from the repository of type E. */
  public List<E> readAll();

  /** Reads all the entities from the repository of type E. with the specified filters */
  public List<E> readAll(Integer maxRows, String sortColumn, Boolean sortAscending);

  /** Updates an entity on the repository. */
  public void update(E entity);

  /** Deletes an entity from the repository. */
  public E delete(Long id);
}
