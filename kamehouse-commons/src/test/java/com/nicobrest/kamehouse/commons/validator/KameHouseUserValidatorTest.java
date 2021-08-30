package com.nicobrest.kamehouse.commons.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.junit.jupiter.api.Test;

/**
 * Test class for the KameHouseUserValidator.
 * 
 * @author nbrest
 *
 */
public class KameHouseUserValidatorTest {


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
    assertThrows(KameHouseInvalidDataException.class, () -> {
      KameHouseUserValidator.validateFirstNameFormat(".Yukimura");
    });
  }

  /**
   * Tests the failure flow of validateLastNameFormat.
   */
  @Test
  public void validateLastNameFormatExceptionTest() {
    assertThrows(KameHouseInvalidDataException.class, () -> {
      KameHouseUserValidator.validateLastNameFormat("Seichi9");
    });
  }
}
