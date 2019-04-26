package com.nicobrest.kamehouse.admin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Utility class to generate and check hashed passwords.
 * 
 * @author nbrest
 *
 */
public class PasswordUtils {
  
  private static final Logger logger = LoggerFactory.getLogger(PasswordUtils.class);
  private static final int LOG_ROUNDS = 12;

  /**
   * Generates a hashed password from a plain text one.
   */
  public static String generateHashedPassword(String plainTextPassword) {
    String hashedPassword = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(LOG_ROUNDS));
    return hashedPassword;
  }
  
  /**
   * Checks if the plain text and hashed passwords match.
   */
  public static boolean isValidPassword(String plainTextPassword, String hashedPassword) {
    boolean isValidPassword = false;
    try {
      isValidPassword = BCrypt.checkpw(plainTextPassword, hashedPassword);
    } catch (IllegalArgumentException e) {
      logger.debug(e.getClass().getSimpleName() + ": " + e.getMessage());
    }
    return isValidPassword;
  }
}
