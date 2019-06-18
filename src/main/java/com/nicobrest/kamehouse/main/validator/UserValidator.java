package com.nicobrest.kamehouse.main.validator;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for User validations.
 * 
 * @author nbrest
 *
 */
public class UserValidator {

  private static final int MAX_STRING_LENGTH = 255;
  //TODO: Consider moving these regex to properties.
  private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
      + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
  private static final String USERNAME_REGEX = "^[A-Za-z0-9]+[\\._A-Za-z0-9-]*";
  private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

  /**
   * Validate that the username respects the established format.
   */
  public static void validateUsernameFormat(String username) {

    Matcher matcher = USERNAME_PATTERN.matcher(username);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid username format: " + username);
    }
  }

  /**
   * Validate that the email has a valid format.
   */
  public static void validateEmailFormat(String email) {

    Matcher matcher = EMAIL_PATTERN.matcher(email);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid email address: " + email);
    }
  }  
  
  /**
   * Validate that the string length is accepted by the database.
   */
  public static void validateStringLength(String value) {

    if (value.length() > MAX_STRING_LENGTH) {
      throw new KameHouseInvalidDataException("The string attribute excedes the maximum length of "
          + MAX_STRING_LENGTH + ". Current length: " + value.length());
    }
  }  
}
