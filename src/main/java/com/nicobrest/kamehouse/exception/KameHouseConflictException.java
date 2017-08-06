package com.nicobrest.kamehouse.exception;

/**
 * KameHouseConflictException class.
 *
 * @author nbrest
 */
public class KameHouseConflictException extends KameHouseException {

  private static final long serialVersionUID = 9L;

  public KameHouseConflictException(String message) {
    super(message);
  }

  public KameHouseConflictException(String message, Exception cause) {
    super(message, cause);
  }
}
