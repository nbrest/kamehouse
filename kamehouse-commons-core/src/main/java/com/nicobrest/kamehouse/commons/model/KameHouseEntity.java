package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.dao.Identifiable;

/**
 * Interface implemented by all kamehouse entities.
 *
 * @author nbrest
 */
public interface KameHouseEntity<D> extends Identifiable {

  /**
   * Builds the DTO for the current entity.
   */
  public D buildDto();
}
