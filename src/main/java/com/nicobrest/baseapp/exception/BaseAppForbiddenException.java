package com.nicobrest.baseapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BaseAppForbiddenException class.
 *
 * @author nbrest
 */
//@ResponseStatus(HttpStatus.FORBIDDEN)
public class BaseAppForbiddenException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppForbiddenException(String message) {
    super(message);
  }

  public BaseAppForbiddenException(String message, Exception cause) {
    super(message, cause);
  }
}
