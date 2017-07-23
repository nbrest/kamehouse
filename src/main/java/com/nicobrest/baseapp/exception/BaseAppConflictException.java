package com.nicobrest.baseapp.exception;

/**
 * BaseAppConflictException class.
 *
 * @author nbrest
 */
public class BaseAppConflictException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppConflictException(String message) {
    super(message);
  }

  public BaseAppConflictException(String message, Exception cause) {
    super(message, cause);
  }
}
