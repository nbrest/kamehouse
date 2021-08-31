package com.nicobrest.kamehouse.testmodule.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.junit.jupiter.api.Test;

/**
 * Test class for the DragonBallUserValidator.
 *
 * @author nbrest
 */
public class DragonBallUserValidatorTest {

  /** Tests valid positive value. Should execute without throwing exceptions. */
  @Test
  public void validatePositiveValueTest() {
    DragonBallUserValidator.validatePositiveValue(9);
  }

  /** Tests the failure flow of validatePositiveValue. */
  @Test
  public void validatePositiveValueExceptionTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          DragonBallUserValidator.validatePositiveValue(-10);
        });
  }
}
