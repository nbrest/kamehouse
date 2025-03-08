package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common Input field validations.
 *
 * @author nbrest
 */
public class InputValidator {

  public static final List<String> FORBIDDEN_CHARS_FOR_SHELL = getForbiddenCharsForShell();

  protected static final Logger LOGGER = LoggerFactory.getLogger(InputValidator.class);
  private static final int MAX_STRING_LENGTH = 255;

  private InputValidator() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Validates that the string length is within the accepted length.
   */
  public static void validateStringLength(String value, int maxLength) {
    if (value.length() > maxLength) {
      String errorMessage =
          "The string attribute exceeds the maximum length of "
              + maxLength
              + ". Current length: "
              + value.length();
      throwInputValidationError(errorMessage);
    }
  }

  /**
   * Validates that the string length is within the accepted length.
   */
  public static void validateStringLength(String value) {
    validateStringLength(value, MAX_STRING_LENGTH);
  }

  /**
   * Validates that the string doesn't contain forbidden characters for shell execution.
   */
  public static void validateForbiddenCharsForShell(String value) {
    if (StringUtils.isEmpty(value)) {
      return;
    }
    FORBIDDEN_CHARS_FOR_SHELL.forEach(forbiddenChar -> {
      if (value.contains(forbiddenChar)) {
        String errorMessage =
            "The string '" + value + "' contains the forbidden character '" + forbiddenChar + "'";
        throwInputValidationError(errorMessage);
      }
    });
  }

  /**
   * Handle validation error.
   */
  public static void throwInputValidationError(String errorMessage) {
    LOGGER.error(errorMessage);
    throw new KameHouseInvalidDataException(errorMessage);
  }

  /**
   * Get the list of forbidden characters to block for kamehouse-shell script arguments.
   *
   * <p>When I update the forbidden chars here I also need to update them in
   * InputValidator.java</p>
   */
  private static List<String> getForbiddenCharsForShell() {
    return List.of(">", "<", ";", "|", "&", "*", "(", ")", "{", "}", "[", "]", "^", "#", "`",
        "´", "..", "%", "!", "$", "?");
  }
}
