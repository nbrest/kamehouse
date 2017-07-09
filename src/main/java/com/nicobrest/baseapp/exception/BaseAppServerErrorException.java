package com.nicobrest.baseapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BaseAppServerErrorException class.
 *
 * @author nbrest
 */
//@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BaseAppServerErrorException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppServerErrorException(String message) {
    super(message);
  }

  public BaseAppServerErrorException(String message, Exception cause) {
    super(message, cause);
  }
}
