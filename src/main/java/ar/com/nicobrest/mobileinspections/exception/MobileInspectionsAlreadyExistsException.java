package ar.com.nicobrest.mobileinspections.exception;

/**
 *        MobileInspectionsAlreadyExistsException class.
 *         
 * @author nbrest
 */
public class MobileInspectionsAlreadyExistsException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsAlreadyExistsException(String message) {
    super(message);
  }
  
  public MobileInspectionsAlreadyExistsException(String message, Exception cause) {
    super(message, cause);
  }
}
