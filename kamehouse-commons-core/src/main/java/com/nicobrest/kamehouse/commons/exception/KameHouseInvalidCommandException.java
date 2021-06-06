package com.nicobrest.kamehouse.commons.exception;

/**
 * KameHouseInvalidCommandException class.
 * 
 * @author nbrest
 */
public class KameHouseInvalidCommandException extends KameHouseException {

  private static final long serialVersionUID = 9L;

  public KameHouseInvalidCommandException(String message) {
    super(message);
  }

  public KameHouseInvalidCommandException(String message, Exception cause) {
    super(message, cause);
  }
}
