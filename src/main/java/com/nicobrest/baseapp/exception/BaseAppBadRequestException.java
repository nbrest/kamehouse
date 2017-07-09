package com.nicobrest.baseapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * BaseAppBadRequestException class.
 *
 * @author nbrest
 */
//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BaseAppBadRequestException extends BaseAppException {

  private static final long serialVersionUID = 9L;

  public BaseAppBadRequestException(String message) {
    super(message);
  }

  public BaseAppBadRequestException(String message, Exception cause) {
    super(message, cause);
  }
}
