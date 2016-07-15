package ar.com.nicobrest.mobileinspections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *        MobileInspectionsNotFoundException class.
 *         
 * @author nbrest
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MobileInspectionsNotFoundException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsNotFoundException(String message) {
    super(message);
  }

  public MobileInspectionsNotFoundException(String message, Exception cause) {
    super(message, cause);
  }
}
