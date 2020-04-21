package com.nicobrest.kamehouse.main.validator;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;
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
  private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
      + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
  private static final String USERNAME_REGEX = "^[A-Za-z0-9]+[\\._A-Za-z0-9-]*";
  private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

  private UserValidator() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Validates that the username respects the established format.
   */
  public static void validateUsernameFormat(String username) {
    Matcher matcher = USERNAME_PATTERN.matcher(username);
    if (!matcher.matches()) {
      String errorMessage = "Invalid username format: " + username;
      LOGGER.error(errorMessage);
      throw new KameHouseInvalidDataException(errorMessage);
    }
  }

  /**
   * Validates that the email has a valid format.
   */
  public static void validateEmailFormat(String email) {
    Matcher matcher = EMAIL_PATTERN.matcher(email);
    if (!matcher.matches()) {
      String errorMessage = "Invalid email address: " + email;
      LOGGER.error(errorMessage);
      throw new KameHouseInvalidDataException(errorMessage);
    }
  }  
  
  /**
   * Validates that the string length is accepted by the database.
   */
  public static void validateStringLength(String value) {
    if (value.length() > MAX_STRING_LENGTH) {
      String errorMessage = "The string attribute excedes the maximum length of "
              + MAX_STRING_LENGTH + ". Current length: " + value.length();
      LOGGER.error(errorMessage);
      throw new KameHouseInvalidDataException(errorMessage);
    }
  }  
}
