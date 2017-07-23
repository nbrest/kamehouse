package com.nicobrest.baseapp.exception;

/**
 * BaseAppServerErrorException class.
 *
 * @author nbrest
 */
public class BaseAppServerErrorException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppServerErrorException(String message) {
    super(message);
  }

  public BaseAppServerErrorException(String message, Exception cause) {
    super(message, cause);
  }
}
