package com.nicobrest.kamehouse.security;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for the password utility class.
 * 
 * @author nbrest
 *
 */
public class PasswordUtilsTest {

  /**
   * Tests the hashed password generation and it's validation with the plain
   * text password.
   */
  @Test
  public void validateGenerateHashedPasswordTest() {

    String plainTextPassword = "SonGoku1234";
    String hashedPassword = PasswordUtils.generateHashedPassword(plainTextPassword);
    assertTrue("Plain and hashed passwords should match", PasswordUtils.isValidPassword(
        plainTextPassword, hashedPassword));
  }
}
