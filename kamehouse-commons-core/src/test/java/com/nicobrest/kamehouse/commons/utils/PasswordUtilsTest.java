package com.nicobrest.kamehouse.commons.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.model.PasswordEntity;
import com.nicobrest.kamehouse.commons.model.TestUserEntity;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for the password utility class.
 *
 * @author nbrest
 */
public class PasswordUtilsTest {

  /**
   * Tests the hashed password generation and it's validation with the plain text password.
   */
  @Test
  public void validateGenerateHashedPasswordTest() {
    String plainTextPassword = "SonGoku1234";

    String hashedPassword = PasswordUtils.generateHashedPassword(plainTextPassword);

    assertTrue(
        "Plain and hashed passwords should match",
        PasswordUtils.isValidPassword(plainTextPassword, hashedPassword));
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

    List<TestUserEntity> nullList = null;
    PasswordUtils.unsetPassword(nullList);
    PasswordUtils.unsetPassword(userEntityList);

    assertNull(userEntityList.get(0).getPassword(), "Password should be null");
  }

  /**
   * Tests removing the password from entities.
   */
  @Test
  public void unsetPasswordByteArrayTest() {
    List<PasswordEntity> userEntityList = new ArrayList<>();
    PasswordEntity<byte[]> entity = new PasswordEntity<>() {
      private byte[] password;

      @Override
      public byte[] getPassword() {
        return password;
      }

      @Override
      public void setPassword(byte[] password) {
        this.password = password;
      }
    };
    entity.setPassword("mada mada".getBytes(StandardCharsets.UTF_8));
    userEntityList.add(entity);
    assertNotNull(userEntityList.get(0).getPassword());

    PasswordUtils.unsetPassword(userEntityList);
    String expectedPassword = new String(new byte[0], StandardCharsets.UTF_8);
    String returnedPassword = new String((byte[]) userEntityList.get(0).getPassword(),
        StandardCharsets.UTF_8);
    assertEquals(expectedPassword, returnedPassword);
  }

  /**
   * Generate null password test.
   */
  @Test
  public void nullPasswordTest() {
    assertThrows(
        KameHouseInvalidDataException.class,
        () -> {
          PasswordUtils.generateHashedPassword(null);
        });
  }

  /**
   * Checks isValid null password test.
   */
  @Test
  public void isValidNullPasswordTest() {
    assertFalse(PasswordUtils.isValidPassword("", "gohan"));
  }
}
