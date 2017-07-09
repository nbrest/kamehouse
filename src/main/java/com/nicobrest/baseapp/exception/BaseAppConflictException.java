package com.nicobrest.baseapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BaseAppConflictException class.
 *
 * @author nbrest
 */
//@ResponseStatus(HttpStatus.CONFLICT)
public class BaseAppConflictException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppConflictException(String message) {
    super(message);
  }

  public BaseAppConflictException(String message, Exception cause) {
    super(message, cause);
  }
}
