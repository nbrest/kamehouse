package com.nicobrest.kamehouse.commons.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.junit.jupiter.api.Test;

/**
 * Test class for the UserValidator.
 *
 * @author nbrest
 */
class UserValidatorTest {

  /** Tests valid username format. Should finish without throwing exceptions. */
  @Test
  void validateUsernameFormatTest() {
    UserValidator.validateUsernameFormat("niko9enzo");
  }

  /** Tests valid email format. Should finish without throwing exceptions. */
  @Test
  void validateEmailFormatTest() {
    UserValidator.validateEmailFormat("niko9enzo@dbz.com");
  }

  /** Tests the failure flow of validateUsernameFormat. */
  @Test
  void validateUsernameFormatExceptionTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          UserValidator.validateUsernameFormat(".goku.9.enzo");
        });
  }

  /** Tests the failure flow of validateEmailFormat. */
  @Test
  void validateEmailFormatExceptionTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          UserValidator.validateEmailFormat("goku.9.enzo@@dbz.com");
        });
  }
}
