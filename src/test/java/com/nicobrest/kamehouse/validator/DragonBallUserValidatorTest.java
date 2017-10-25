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
   * Test valid user format.
   */
  @Test
  public void validateUserFormatTest() {
    try {
      DragonBallUserValidator.validateUsernameFormat("niko9enzo");  
    } catch (Exception e) {
      fail("Unexpected exception thrown.");
    } 
  }
  
  /**
   * Test valid email format.
   */
  @Test
  public void validateEmailFormatTest() {
    try {
      DragonBallUserValidator.validateEmailFormat("niko9enzo@dbz.com");
    } catch (Exception e) {
      fail("Unexpected exception thrown.");
    }
  }
  
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
   * Test valid string length.
   */
  @Test
  public void validateStringLength() {
    try {
      DragonBallUserValidator.validateStringLength("mada mada dane");
    } catch (Exception e) {
      fail("Unexpected exception thrown.");
    }
  }
  
  /**
   * Test the failure flow of validateUsernameFormat.
   */
  @Test
  public void validateUsernameFormatExceptionTest() {
    
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid username format:");
    DragonBallUserValidator.validateUsernameFormat(".goku.9.enzo"); 
  }
  
  /**
   * Test the failure flow of validateEmailFormat.
   */
  @Test
  public void validateEmailFormatExceptionTest() { 
    
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid email address: ");
    DragonBallUserValidator.validateEmailFormat("goku.9.enzo@@dbz.com");
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
  
  /**
   * Test the failure flow of validateStringLength.
   */
  @Test
  public void validateStringLengthExceptionTest() {  
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("The string attribute excedes the maximum length of ");
    
    StringBuilder sb = new StringBuilder();
    for (int i = 0 ; i < 70 ; i++) {
      sb.append("goku");
    }
    String username = sb.toString();
    DragonBallUserValidator.validateStringLength(username);
  }
}
