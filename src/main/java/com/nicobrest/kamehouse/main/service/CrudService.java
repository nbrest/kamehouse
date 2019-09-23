package com.nicobrest.kamehouse.main.service;

public interface CrudService<E, D> {

  /**
   * Creates a new Entity in the repository from it's DTO.
   */
  Long create(D dto);
}
