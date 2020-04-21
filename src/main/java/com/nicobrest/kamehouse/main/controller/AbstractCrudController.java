package com.nicobrest.kamehouse.main.controller;

import com.nicobrest.kamehouse.main.dao.Identifiable;
import com.nicobrest.kamehouse.main.service.CrudService;

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
  protected <D, E> ResponseEntity<Long> create(String endpoint, CrudService<E, D> service, D dto) {
    logger.trace("{} (POST) body {}", endpoint, dto.toString());
    Long createdId = service.create(dto);
    return generatePostResponseEntity(createdId);
  }

  /**
   * Reads an entity from the repository for the specified id.
   */
  protected <D, E> ResponseEntity<E> read(String endpoint, CrudService<E, D> service, Long id) {
    logger.trace("{} (GET)", endpoint);
    E entity = service.read(id);
    return generateGetResponseEntity(entity);
  }

  /**
   * Reads all the entities from the repository for the specified service.
   */
  protected <D, E> ResponseEntity<List<E>> readAll(String endpoint, CrudService<E, D> service) {
    logger.trace("{} (GET)", endpoint);
    List<E> entitiesList = service.readAll();
    return generateGetResponseEntity(entitiesList);
  }

  /**
   * Updates an entity in the repository for the specified id and dto.
   */
  protected <D, E> ResponseEntity<Void> update(String endpoint, CrudService<E, D> service, Long id,
      D dto) {
    logger.trace("{} (PUT) body {}", endpoint, dto.toString());
    Identifiable identifiableDto = (Identifiable) dto;
    validatePathAndRequestBodyIds(id, identifiableDto.getId());
    service.update(dto);
    return generatePutResponseEntity();
  }

  /**
   * Deletes an entity from the repository for the specified id.
   */
  protected <E, D> ResponseEntity<E> delete(String endpoint, CrudService<E, D> service, Long id) {
    logger.trace("{} (DELETE)", endpoint);
    E deletedEntity = service.delete(id);
    return generateDeleteResponseEntity(deletedEntity);
  }
}
