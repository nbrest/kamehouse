package com.nicobrest.kamehouse.exception;

/**
 * KameHouseBadRequestException class.
 *
 * @author nbrest
 */
public class KameHouseBadRequestException extends KameHouseException {

  private static final long serialVersionUID = 9L;

  public KameHouseBadRequestException(String message) {
    super(message);
  }

  public KameHouseBadRequestException(String message, Exception cause) {
    super(message, cause);
  }
}
