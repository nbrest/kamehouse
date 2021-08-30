package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.nicobrest.kamehouse.commons.model.TestUserEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

  /**
   * Tests removing the password from entities.
   */
  @Test
  public void unsetPasswordTest() {
    List<TestUserEntity> userEntityList = new ArrayList<>();
    TestUserEntity entity = new TestUserEntity();
    entity.setName("gohan");
    entity.setPassword("mada mada dane");
    userEntityList.add(entity);
    assertNotNull(userEntityList.get(0).getPassword());

    PasswordUtils.unsetPassword(userEntityList);

    assertNull(userEntityList.get(0).getPassword(), "Password should be null");
  }
}
