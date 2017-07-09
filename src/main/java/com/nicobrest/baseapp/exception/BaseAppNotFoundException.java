package com.nicobrest.baseapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BaseAppNotFoundException class.
 *
 * @author nbrest
 */
//@ResponseStatus(HttpStatus.NOT_FOUND)
public class BaseAppNotFoundException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppNotFoundException(String message) {
    super(message);
  }

  public BaseAppNotFoundException(String message, Exception cause) {
    super(message, cause);
  }
}
