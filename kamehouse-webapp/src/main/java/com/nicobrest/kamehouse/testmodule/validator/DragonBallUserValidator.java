package com.nicobrest.kamehouse.testmodule.validator;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to validate the attributes of a DragonBallUser.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserValidator {

  protected static final Logger LOGGER = LoggerFactory.getLogger(DragonBallUserValidator.class);

  private DragonBallUserValidator() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Validates that the integer has a positive value.
   */
  public static void validatePositiveValue(int value) {
    if (value < 0) {
      String errorMessage = "The attribute should be a positive value. Current value: " + value;
      LOGGER.error(errorMessage);
      throw new KameHouseInvalidDataException(errorMessage);
    }
  }
}
