package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.dao.Identifiable;

/**
 * Interface implemented by entities that are identifiable and contain passwords.
 * Usually implemented by user entities (KameHouseUser, TennisWorldUser for example).
 * The password parameter type would usually be String unless it's encrypted, in which case it
 * could be a byte[].
 *
 * @author nbrest
 *
 */
public interface IdentifiablePasswordEntity<P> extends Identifiable {

  /**
   * Gets the password of the entity.
   */
  public P getPassword();
  
  /**
   * Sets the password of the entity.
   */
  public void setPassword(P password);
}
