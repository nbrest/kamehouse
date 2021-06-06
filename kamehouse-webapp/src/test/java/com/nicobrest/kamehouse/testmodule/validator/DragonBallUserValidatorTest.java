package com.nicobrest.kamehouse.testmodule.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for the DragonBallUserValidator.
 * 
 * @author nbrest
 *
 */
public class DragonBallUserValidatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Tests valid positive value. Should execute without throwing exceptions.
   */
  @Test
  public void validatePositiveValueTest() {
    DragonBallUserValidator.validatePositiveValue(9);
  }

  /**
   * Tests the failure flow of validatePositiveValue.
   */
  @Test
  public void validatePositiveValueExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("The attribute should be a positive value. Current value:");

    DragonBallUserValidator.validatePositiveValue(-10);
  }
}
