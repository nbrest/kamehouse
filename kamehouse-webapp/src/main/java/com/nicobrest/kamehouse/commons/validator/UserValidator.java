package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common User validations.
 * 
 * @author nbrest
 *
 */
public class UserValidator {

  protected static final Logger LOGGER = LoggerFactory.getLogger(UserValidator.class);
  private static final int MAX_STRING_LENGTH = 255;
  private static final String EMAIL_REGEX = "^[_A-Za-z0-9]+[\\._\\-A-Za-z0-9]*@"
      + "[A-Za-z0-9]+\\.[A-Za-z]{2,}$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
  private static final String USERNAME_REGEX = "^[A-Za-z0-9]+[\\._\\-A-Za-z0-9]*";
  private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
  private static final String INVALID_USERNAME = "Invalid username format: ";
  private static final String INVALID_EMAIL = "Invalid email address: ";

  private UserValidator() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Validates that the username respects the established format.
   */
  public static void validateUsernameFormat(String username) {
    if (username == null) {
      handleError(INVALID_USERNAME);
    }
    Matcher matcher = USERNAME_PATTERN.matcher(username);
    if (!matcher.matches()) {
      handleError(INVALID_USERNAME + username);
    }
  }

  /**
   * Validates that the email has a valid format.
   */
  public static void validateEmailFormat(String email) {
    if (email == null) {
      handleError(INVALID_EMAIL);
    }
    Matcher matcher = EMAIL_PATTERN.matcher(email);
    if (!matcher.matches()) {
      handleError(INVALID_EMAIL + email);
    }
  }  
  
  /**
   * Validates that the string length is accepted by the database.
   */
  public static void validateStringLength(String value) {
    if (value.length() > MAX_STRING_LENGTH) {
      String errorMessage = "The string attribute excedes the maximum length of "
              + MAX_STRING_LENGTH + ". Current length: " + value.length();
      handleError(errorMessage);
    }
  }

  /**
   * Handle validation error.
   */
  private static void handleError(String errorMessage) {
    LOGGER.error(errorMessage);
    throw new KameHouseInvalidDataException(errorMessage);
  }
}
