package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to validate the attributes of a KameHouseUser.
 * 
 * @author nbrest
 *
 */
public class KameHouseUserValidator {

  protected static final Logger LOGGER = LoggerFactory.getLogger(KameHouseUserValidator.class);
  private static final String FIRST_NAME_REGEX = "^[A-Za-z]+";
  private static final Pattern FIRST_NAME_PATTERN = Pattern.compile(FIRST_NAME_REGEX);
  private static final String LAST_NAME_REGEX = "^[A-Za-z]+";
  private static final Pattern LAST_NAME_PATTERN = Pattern.compile(LAST_NAME_REGEX);

  private KameHouseUserValidator() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Validates that the first name has a valid format.
   */
  public static void validateFirstNameFormat(String firstName) {
    Matcher matcher = FIRST_NAME_PATTERN.matcher(firstName);
    if (!matcher.matches()) {
      String errorMessage = "Invalid first name: " + firstName;
      LOGGER.error(errorMessage);
      throw new KameHouseInvalidDataException(errorMessage);
    }
  }   

  /**
   * Validates that the last name respects the established format.
   */
  public static void validateLastNameFormat(String lastName) {
    Matcher matcher = LAST_NAME_PATTERN.matcher(lastName);
    if (!matcher.matches()) {
      String errorMessage = "Invalid last name: " + lastName;
      LOGGER.error(errorMessage);
      throw new KameHouseInvalidDataException(errorMessage);
    }
  }
}
