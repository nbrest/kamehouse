package ar.com.nicobrest.mobileinspections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * MobileInspectionsForbiddenException class.
 * 
 * @author nbrest
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class MobileInspectionsForbiddenException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsForbiddenException(String message) {
    super(message);
  }

  public MobileInspectionsForbiddenException(String message, Exception cause) {
    super(message, cause);
  }
}
