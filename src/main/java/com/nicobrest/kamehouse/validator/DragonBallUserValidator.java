package com.nicobrest.kamehouse.validator;

import com.nicobrest.kamehouse.exception.KameHouseInvalidDataException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to validate the attributes of a DragonBallUser.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserValidator {
  
  private static final int MAX_STRING_LENGTH = 255;
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
   * Validate that the integer has a positive value.
   */
  public static void validatePositiveValue(int value) {

    if (value < 0) {
      throw new KameHouseInvalidDataException(
          "The attribute should be a positive value. Current value: " + value);
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
