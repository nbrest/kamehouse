package com.nicobrest.baseapp.exception;

/**
 * BaseAppBadRequestException class.
 *
 * @author nbrest
 */
public class BaseAppBadRequestException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppBadRequestException(String message) {
    super(message);
  }

  public BaseAppBadRequestException(String message, Exception cause) {
    super(message, cause);
  }
}
