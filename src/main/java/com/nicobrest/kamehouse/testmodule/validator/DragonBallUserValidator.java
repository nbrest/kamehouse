package com.nicobrest.kamehouse.testmodule.validator;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.main.validator.UserValidator;

/**
 * Class to validate the attributes of a DragonBallUser.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserValidator extends UserValidator {
  
  /**
   * Validate that the integer has a positive value.
   */
  public static void validatePositiveValue(int value) {

    if (value < 0) {
      throw new KameHouseInvalidDataException(
          "The attribute should be a positive value. Current value: " + value);
    }
  }
}
