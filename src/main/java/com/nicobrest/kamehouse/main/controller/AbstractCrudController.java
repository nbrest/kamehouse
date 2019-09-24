package com.nicobrest.kamehouse.main.controller;

import com.nicobrest.kamehouse.main.service.CrudService;

import org.springframework.http.ResponseEntity;

/**
 * Abstract class to group all CRUD functionality in the controller layer.
 * 
 * @author nbrest
 *
 */
public abstract class AbstractCrudController extends AbstractController {

  /**
   * Creates a new entity in the repository from the DTO.
   */
  public <D, E> ResponseEntity<Long> create(String endpoint, CrudService<E, D> service, D dto) {
    logger.trace("{} (POST)", endpoint);
    Long createdId = service.create(dto);
    return generatePostResponseEntity(createdId);
  }

  /**
   * Reads an entity from the repository for the specified id.
   */
  public <D, E> ResponseEntity<E> read(String endpoint, CrudService<E, D> service, Long id) {
    logger.trace("{} (GET)", endpoint);
    E entity = service.read(id);
    return generateGetResponseEntity(entity);
  }
}
