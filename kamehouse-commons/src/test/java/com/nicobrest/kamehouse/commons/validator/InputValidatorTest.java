package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for the InputValidator.
 * 
 * @author nbrest
 *
 */
public class InputValidatorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Tests valid string length. Should finish without throwing exceptions.
   */
  @Test
  public void validateStringLength() {
    InputValidator.validateStringLength("mada mada dane");
  }

  /**
   * Tests the failure flow of validateStringLength.
   */
  @Test
  public void validateStringLengthExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("The string attribute excedes the maximum length of ");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 70; i++) {
      sb.append("goku");
    }
    String username = sb.toString();

    InputValidator.validateStringLength(username);
  }
}
