package com.nicobrest.kamehouse.commons.dao;

/**
 * Interface for persistable model objects that need to be able to be
 * identified.
 *
 * <p>Duplicated definition in modules:
 *  - kamehouse-commons
 *  - kamehouse-commons-test
 * </p>
 *
 * @author nbrest
 *
 */
public interface Identifiable {

  /**
   * Gets the id of the entity.
   */
  public Long getId();
  
  /**
   * Sets the id of the entity.
   */
  public void setId(Long id);
}
