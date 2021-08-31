package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common Input field validations.
 *
 * @author nbrest
 */
public class InputValidator {

  protected static final Logger LOGGER = LoggerFactory.getLogger(InputValidator.class);
  private static final int MAX_STRING_LENGTH = 255;

  private InputValidator() {
    throw new IllegalStateException("Utility class");
  }

  /** Validates that the string length is within the accepted length. */
  public static void validateStringLength(String value, int maxLength) {
    if (value.length() > maxLength) {
      String errorMessage =
          "The string attribute excedes the maximum length of "
              + maxLength
              + ". Current length: "
              + value.length();
      throwInputValidationError(errorMessage);
    }
  }

  /** Validates that the string length is within the accepted length. */
  public static void validateStringLength(String value) {
    validateStringLength(value, MAX_STRING_LENGTH);
  }

  /** Handle validation error. */
  public static void throwInputValidationError(String errorMessage) {
    LOGGER.error(errorMessage);
    throw new KameHouseInvalidDataException(errorMessage);
  }
}
