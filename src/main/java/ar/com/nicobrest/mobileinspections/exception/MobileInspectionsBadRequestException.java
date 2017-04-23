package ar.com.nicobrest.mobileinspections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * MobileInspectionsBadRequestException class.
 *
 * @author nbrest
 */
//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MobileInspectionsBadRequestException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsBadRequestException(String message) {
    super(message);
  }

  public MobileInspectionsBadRequestException(String message, Exception cause) {
    super(message, cause);
  }
}
