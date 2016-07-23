package ar.com.nicobrest.mobileinspections.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * MobileInspectionsServerErrorException class.
 * 
 * @author nbrest
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MobileInspectionsServerErrorException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsServerErrorException(String message) {
    super(message);
  }

  public MobileInspectionsServerErrorException(String message, Exception cause) {
    super(message, cause);
  }
}
