package com.nicobrest.kamehouse.commons.validator;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
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
   * Tests valid username format. Should finish without throwing exceptions.
   */
  @Test
  public void validateUsernameFormatTest() {
    UserValidator.validateUsernameFormat("niko9enzo");
  }

  /**
   * Tests valid email format. Should finish without throwing exceptions.
   */
  @Test
  public void validateEmailFormatTest() {
    UserValidator.validateEmailFormat("niko9enzo@dbz.com");
  }

  /**
   * Tests the failure flow of validateUsernameFormat.
   */
  @Test
  public void validateUsernameFormatExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid username format:");

    UserValidator.validateUsernameFormat(".goku.9.enzo");
  }

  /**
   * Tests the failure flow of validateEmailFormat.
   */
  @Test
  public void validateEmailFormatExceptionTest() {
    thrown.expect(KameHouseInvalidDataException.class);
    thrown.expectMessage("Invalid email address: ");

    UserValidator.validateEmailFormat("goku.9.enzo@@dbz.com");
  }
}
