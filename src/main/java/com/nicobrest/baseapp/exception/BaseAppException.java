package com.nicobrest.baseapp.exception;

/**
 * BaseAppException base exceptions class.
 * 
 * @author nbrest
 */
public class BaseAppException extends RuntimeException {

  private static final long serialVersionUID = 9L;

  public BaseAppException(String message) {
    super(message);
  }

  public BaseAppException(String message, Exception cause) {
    super(message, cause);
  }
}
