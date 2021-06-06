package com.nicobrest.kamehouse.commons.exception;

/**
 * KameHouseServerErrorException class.
 *
 * @author nbrest
 */
public class KameHouseServerErrorException extends KameHouseException {

  private static final long serialVersionUID = 9L;

  public KameHouseServerErrorException(String message) {
    super(message);
  }

  public KameHouseServerErrorException(String message, Exception cause) {
    super(message, cause);
  }
}
