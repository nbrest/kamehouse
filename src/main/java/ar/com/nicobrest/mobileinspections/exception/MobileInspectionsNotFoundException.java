package ar.com.nicobrest.mobileinspections.exception;

/**
 *        MobileInspectionsNotFoundException class.
 *         
 * @author nbrest
 */
public class MobileInspectionsNotFoundException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsNotFoundException(String message) {
    super(message);
  }

  public MobileInspectionsNotFoundException(String message, Exception cause) {
    super(message, cause);
  }
}
