package com.nicobrest.kamehouse.main.validator;

import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.main.validator.UserValidator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for the UserValidator.
 * 
 * @author nbrest
 *
 */
public class UserValidatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Test valid username format.
   */
  @Test
  public void validateUsernameFormatTest() {
    try {
      UserValidator.validateUsernameFormat("niko9enzo");  
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
      UserValidator.validateEmailFormat("niko9enzo@dbz.com");
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
      UserValidator.validateStringLength("mada mada dane");
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
    UserValidator.validateUsernameFormat(".goku.9.enzo"); 
  }
  
  /**
   * Test the failure flow of validateEmailFormat.
   */
  @Test
  public void validateEmailFormatExceptionTest() { 
    
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid email address: ");
    UserValidator.validateEmailFormat("goku.9.enzo@@dbz.com");
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
    UserValidator.validateStringLength(username);
  }
}
