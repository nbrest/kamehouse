package ar.com.nicobrest.mobileinspections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * MobileInspectionsConflictException class.
 *
 * @author nbrest
 */
//@ResponseStatus(HttpStatus.CONFLICT)
public class MobileInspectionsConflictException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsConflictException(String message) {
    super(message);
  }

  public MobileInspectionsConflictException(String message, Exception cause) {
    super(message, cause);
  }
}
