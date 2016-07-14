package ar.com.nicobrest.mobileinspections.exception;

/**
 *        MobileInspectionsForbiddenException class.
 *         
 * @author nbrest
 */
public class MobileInspectionsForbiddenException extends MobileInspectionsException {

  private static final long serialVersionUID = 9L;

  public MobileInspectionsForbiddenException(String message) {
    super(message);
  }

  public MobileInspectionsForbiddenException(String message, Exception cause) {
    super(message, cause);
  }
}
