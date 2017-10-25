package com.nicobrest.kamehouse.validator;

import com.nicobrest.kamehouse.exception.KameHouseInvalidDataException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to validate the attributes of a ApplicationUser.
 * 
 * @author nbrest
 *
 */
public class ApplicationUserValidator extends UserValidator {
  
  private static final String FIRST_NAME_REGEX = "^[A-Za-z]+";
  private static final Pattern FIRST_NAME_PATTERN = Pattern.compile(FIRST_NAME_REGEX);
  private static final String LAST_NAME_REGEX = "^[A-Za-z]+";
  private static final Pattern LAST_NAME_PATTERN = Pattern.compile(LAST_NAME_REGEX);

  /**
   * Validate that the first name has a valid format.
   */
  public static void validateFirstNameFormat(String firstName) {

    Matcher matcher = FIRST_NAME_PATTERN.matcher(firstName);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid first name: " + firstName);
    }
  }   

  /**
   * Validate that the last name respects the established format.
   */
  public static void validateLastNameFormat(String lastName) {

    Matcher matcher = LAST_NAME_PATTERN.matcher(lastName);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid last name: " + lastName);
    }
  }
}
