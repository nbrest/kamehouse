package com.nicobrest.kamehouse.commons.exception;

/**
 * KameHouseException base exceptions class.
 * 
 * @author nbrest
 */
public class KameHouseException extends RuntimeException {

  private static final long serialVersionUID = 9L;

  public KameHouseException(String message) {
    super(message);
  }

  public KameHouseException(String message, Exception cause) {
    super(message, cause);
  }
  
  public KameHouseException(Exception cause) {
    super(cause);
  }
}
