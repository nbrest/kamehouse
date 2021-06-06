package com.nicobrest.kamehouse.commons.exception;

/**
 * KameHouseNotFoundException class.
 *
 * @author nbrest
 */
public class KameHouseNotFoundException extends KameHouseException {

  private static final long serialVersionUID = 9L;

  public KameHouseNotFoundException(String message) {
    super(message);
  }
  
  public KameHouseNotFoundException(String message, Exception cause) {
    super(message, cause);
  }
}
