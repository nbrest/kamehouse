package com.nicobrest.kamehouse.commons.model.dto;

import com.nicobrest.kamehouse.commons.dao.Identifiable;

/**
 * Interface implemented by all kamehouse DTOs.
 *
 * @author nbrest
 */
public interface KameHouseDto<E> extends Identifiable {

  /**
   * Builds the entity for the current DTO.
   */
  public E buildEntity();
}
