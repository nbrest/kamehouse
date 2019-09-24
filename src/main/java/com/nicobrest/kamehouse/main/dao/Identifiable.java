package com.nicobrest.kamehouse.main.dao;

/**
 * Interface for persistable model objects that need to be able to be
 * identified.
 * 
 * @author nbrest
 *
 */
public interface Identifiable {

  /**
   * Get the id of the entity.
   */
  public Long getId();
  
  /**
   * Set the id of the entity.
   */
  public void setId(Long id);
}
