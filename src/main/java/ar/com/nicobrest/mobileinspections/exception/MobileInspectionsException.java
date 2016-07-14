package ar.com.nicobrest.mobileinspections.exception;

/**
 *        MobileInspectionsException base exceptions class.
 *         
 * @author nbrest
 */
public class MobileInspectionsException extends RuntimeException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsException(String message) {
    super(message);
  }

  public MobileInspectionsException(String message, Exception cause) {
    super(message, cause);
  }
}
