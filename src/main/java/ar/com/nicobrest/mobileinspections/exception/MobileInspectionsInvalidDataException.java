package ar.com.nicobrest.mobileinspections.exception;

/**
 * MobileInspectionsInvalidDataException class.
 * 
 * @author nbrest
 */
public class MobileInspectionsInvalidDataException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsInvalidDataException(String message) {
    super(message);
  }

  public MobileInspectionsInvalidDataException(String message, Exception cause) {
    super(message, cause);
  }
}
