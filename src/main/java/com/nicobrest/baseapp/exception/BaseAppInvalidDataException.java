package com.nicobrest.baseapp.exception;

/**
 * BaseAppInvalidDataException class.
 * 
 * @author nbrest
 */
public class BaseAppInvalidDataException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppInvalidDataException(String message) {
    super(message);
  }

  public BaseAppInvalidDataException(String message, Exception cause) {
    super(message, cause);
  }
}
