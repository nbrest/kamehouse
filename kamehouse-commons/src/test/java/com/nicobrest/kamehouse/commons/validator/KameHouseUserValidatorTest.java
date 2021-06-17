package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for the KameHouseUserValidator.
 * 
 * @author nbrest
 *
 */
public class KameHouseUserValidatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Tests valid first name format. Should finish without throwing exceptions.
   */
  @Test
  public void validateFirstNameFormatTest() {
    KameHouseUserValidator.validateFirstNameFormat("Yukimura");
  }

  /**
   * Tests valid last name format. Should finish without throwing exceptions.
   */
  @Test
  public void validateLastNameFormatTest() {
    KameHouseUserValidator.validateLastNameFormat("Seichi");
  }

  /**
   * Tests the failure flow of validateFirstNameFormat.
   */
  @Test
  public void validateFirstNameFormatExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid first name:");

    KameHouseUserValidator.validateFirstNameFormat(".Yukimura");
  }

  /**
   * Tests the failure flow of validateLastNameFormat.
   */
  @Test
  public void validateLastNameFormatExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid last name: ");

    KameHouseUserValidator.validateLastNameFormat("Seichi9");
  }
}
