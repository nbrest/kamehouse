package com.nicobrest.baseapp.exception;

/**
 * BaseAppNotFoundException class.
 *
 * @author nbrest
 */
public class BaseAppNotFoundException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppNotFoundException(String message) {
    super(message);
  }

  public BaseAppNotFoundException(String message, Exception cause) {
    super(message, cause);
  }
}
