package com.nicobrest.kamehouse.validator;

import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.exception.KameHouseInvalidDataException;

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
   * Test valid positive value.
   */
  @Test
  public void validatePositiveValueTest() {
    try {
      DragonBallUserValidator.validatePositiveValue(9);
    } catch (Exception e) {
      fail("Unexpected exception thrown.");
    }
  }
  
  /**
   * Test the failure flow of validatePositiveValue.
   */
  @Test
  public void validatePositiveValueExceptionTest() {
    
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("The attribute should be a positive value. Current value:");
    DragonBallUserValidator.validatePositiveValue(-10);
  }
}
