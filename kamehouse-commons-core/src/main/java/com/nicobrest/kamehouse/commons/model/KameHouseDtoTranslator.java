package com.nicobrest.kamehouse.commons.model;

/**
 * Translator between kamehouse entities and dtos.
 *
 * @author nbrest
 */
public interface KameHouseDtoTranslator<E, D> {

  /**
   * Builds the entity for the current dto.
   */
  public E buildEntity(D dto);

  /**
   * Builds the dto for the current entity.
   */
  public D buildDto(E entity);

}
