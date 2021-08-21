package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.service.CrudService;

import org.springframework.http.ResponseEntity;

import java.util.List;

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
  protected <D, E> ResponseEntity<Long> create(CrudService<E, D> service, D dto) {
    Long createdId = service.create(dto);
    return generatePostResponseEntity(createdId);
  }

  /**
   * Reads an entity from the repository for the specified id.
   */
  protected <D, E> ResponseEntity<E> read(CrudService<E, D> service, Long id) {
    E entity = service.read(id);
    return generateGetResponseEntity(entity);
  }

  /**
   * Reads all the entities from the repository for the specified service.
   */
  protected <D, E> ResponseEntity<List<E>> readAll(CrudService<E, D> service) {
    List<E> entitiesList = service.readAll();
    return generateGetResponseEntity(entitiesList);
  }

  /**
   * Updates an entity in the repository for the specified id and dto.
   */
  protected <D, E> ResponseEntity<Void> update(CrudService<E, D> service, Long id, D dto) {
    Identifiable identifiableDto = (Identifiable) dto;
    validatePathAndRequestBodyIds(id, identifiableDto.getId());
    service.update(dto);
    return generatePutResponseEntity();
  }

  /**
   * Deletes an entity from the repository for the specified id.
   */
  protected <E, D> ResponseEntity<E> delete(CrudService<E, D> service, Long id) {
    E deletedEntity = service.delete(id);
    return generateDeleteResponseEntity(deletedEntity);
  }
}
