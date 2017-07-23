package com.nicobrest.baseapp.exception;

/**
 * BaseAppForbiddenException class.
 *
 * @author nbrest
 */
public class BaseAppForbiddenException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppForbiddenException(String message) {
    super(message);
  }

  public BaseAppForbiddenException(String message, Exception cause) {
    super(message, cause);
  }
}
