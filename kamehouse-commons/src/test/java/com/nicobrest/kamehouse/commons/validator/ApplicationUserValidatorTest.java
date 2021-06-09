package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
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
   * Tests valid first name format. Should finish without throwing exceptions.
   */
  @Test
  public void validateFirstNameFormatTest() {
    ApplicationUserValidator.validateFirstNameFormat("Yukimura");
  }

  /**
   * Tests valid last name format. Should finish without throwing exceptions.
   */
  @Test
  public void validateLastNameFormatTest() {
    ApplicationUserValidator.validateLastNameFormat("Seichi");
  }

  /**
   * Tests the failure flow of validateFirstNameFormat.
   */
  @Test
  public void validateFirstNameFormatExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid first name:");

    ApplicationUserValidator.validateFirstNameFormat(".Yukimura");
  }

  /**
   * Tests the failure flow of validateLastNameFormat.
   */
  @Test
  public void validateLastNameFormatExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid last name: ");

    ApplicationUserValidator.validateLastNameFormat("Seichi9");
  }
}
