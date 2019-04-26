package com.nicobrest.kamehouse.main.exception;

/**
 * KameHouseForbiddenException class.
 *
 * @author nbrest
 */
public class KameHouseForbiddenException extends KameHouseException {

  private static final long serialVersionUID = 9L;

  public KameHouseForbiddenException(String message) {
    super(message);
  }

  public KameHouseForbiddenException(String message, Exception cause) {
    super(message, cause);
  }
}
