package com.nicobrest.kamehouse.validator;

import static org.junit.Assert.fail;

import com.nicobrest.kamehouse.exception.KameHouseInvalidDataException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for the ApplicationUserValidator.
 * 
 * @author nbrest
 *
 */
public class ApplicationUserValidatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Test valid first name format.
   */
  @Test
  public void validateFirstNameFormatTest() {
    try {
      ApplicationUserValidator.validateFirstNameFormat("Yukimura");  
    } catch (Exception e) {
      fail("Unexpected exception thrown.");
    } 
  }
  
  /**
   * Test valid last name format.
   */
  @Test
  public void validateLastNameFormatTest() {
    try {
      ApplicationUserValidator.validateLastNameFormat("Seichi");
    } catch (Exception e) {
      fail("Unexpected exception thrown.");
    }
  }
  
  /**
   * Test the failure flow of validateFirstNameFormat.
   */
  @Test
  public void validateFirstNameFormatExceptionTest() {
    
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid first name:");
    ApplicationUserValidator.validateFirstNameFormat(".Yukimura"); 
  }
  
  /**
   * Test the failure flow of validateLastNameFormat.
   */
  @Test
  public void validateLastNameFormatExceptionTest() { 
    
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid last name: ");
    ApplicationUserValidator.validateLastNameFormat("Seichi9");
  }
}
