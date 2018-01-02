package com.nicobrest.kamehouse.exception;

/**
 * KameHouseInvalidDataException class.
 * 
 * @author nbrest
 */
public class KameHouseInvalidDataException extends KameHouseException {

  private static final long serialVersionUID = 9L;

  public KameHouseInvalidDataException(String message) {
    super(message);
  }

  public KameHouseInvalidDataException(String message, Exception cause) {
    super(message, cause);
  }
}
