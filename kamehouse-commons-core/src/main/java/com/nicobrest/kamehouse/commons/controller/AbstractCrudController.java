package com.nicobrest.kamehouse.commons.controller;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.service.CrudService;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * Abstract class to group all CRUD functionality in the controller layer.
 *
 * @author nbrest
 */
public abstract class AbstractCrudController<E, D> extends AbstractController {

  /**
   * Get crud service.
   */
  public abstract CrudService<E, D> getCrudService();

  /**
   * Creates a new entity in the repository from the DTO.
   */
  protected ResponseEntity<Long> create(D dto) {
    logger.debug("Create {}", dto.getClass().getSimpleName());
    Long createdId = getCrudService().create(dto);
    return generatePostResponseEntity(createdId);
  }

  /**
   * Reads an entity from the repository for the specified id.
   */
  protected ResponseEntity<E> read(Long id) {
    logger.debug("Read {}", id);
    E entity = getCrudService().read(id);
    return generateGetResponseEntity(entity);
  }

  /**
   * Reads all the entities from the repository for the specified service.
   */
  protected ResponseEntity<List<E>> readAll() {
    logger.debug("Read all");
    List<E> entitiesList = getCrudService().readAll();
    return generateGetResponseEntity(entitiesList);
  }

  /**
   * Updates an entity in the repository for the specified id and dto.
   */
  protected ResponseEntity<Void> update(Long id, D dto) {
    logger.debug("Update {}", id);
    Identifiable identifiableDto = (Identifiable) dto;
    validatePathAndRequestBodyIds(id, identifiableDto.getId());
    getCrudService().update(dto);
    return generatePutResponseEntity();
  }

  /**
   * Deletes an entity from the repository for the specified id.
   */
  protected ResponseEntity<E> delete(Long id) {
    logger.debug("Delete {}", id);
    E deletedEntity = getCrudService().delete(id);
    return generateDeleteResponseEntity(deletedEntity);
  }
}
