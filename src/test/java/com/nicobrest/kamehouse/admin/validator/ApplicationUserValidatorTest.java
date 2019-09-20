package com.nicobrest.kamehouse.admin.validator;

import com.nicobrest.kamehouse.main.exception.KameHouseInvalidDataException;

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
   * Test valid first name format. Should finish without throwing exceptions.
   */
  @Test
  public void validateFirstNameFormatTest() {
    ApplicationUserValidator.validateFirstNameFormat("Yukimura");
  }

  /**
   * Test valid last name format. Should finish without throwing exceptions.
   */
  @Test
  public void validateLastNameFormatTest() {
    ApplicationUserValidator.validateLastNameFormat("Seichi");
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
