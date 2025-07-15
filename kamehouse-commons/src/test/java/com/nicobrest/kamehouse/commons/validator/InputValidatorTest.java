package com.nicobrest.kamehouse.commons.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.junit.jupiter.api.Test;

/**
 * Test class for the InputValidator.
 *
 * @author nbrest
 */
class InputValidatorTest {

  /**
   * Tests valid string length. Should finish without throwing exceptions.
   */
  @Test
  void validateStringLengthTest() {
    InputValidator.validateStringLength("mada mada dane");
  }

  /**
   * Tests the failure flow of validateStringLength.
   */
  @Test
  void validateStringLengthExceptionTest() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 70; i++) {
      sb.append("goku");
    }
    String username = sb.toString();
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          InputValidator.validateStringLength(username);
        });
  }

  /**
   * Tests the success flow of validateForbiddenCharsForShell.
   */
  @Test
  void validateForbiddenCharsForShellSuccessTest() {
    String arg = "-m module ";
    assertDoesNotThrow(() -> {
      InputValidator.validateForbiddenCharsForShell(arg);
    });
  }

  /**
   * Tests the failure flow of validateForbiddenCharsForShell.
   */
  @Test
  void validateForbiddenCharsForShellExceptionTest() {
    InputValidator.getForbiddenCharsForShell().forEach(forbiddenChar -> {
      String arg = "-m module " + forbiddenChar;
      assertThrows(
          KameHouseInvalidDataException.class,
          () -> {
            InputValidator.validateForbiddenCharsForShell(arg);
          });
    });
  }
}
