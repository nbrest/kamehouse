package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.model.IdentifiableUserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;

/**
 * Utility class to generate and check hashed passwords.
 * 
 * @author nbrest
 *
 */
public class PasswordUtils {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordUtils.class);
  private static final int LOG_ROUNDS = 12;

  private PasswordUtils() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Generates a hashed password from a plain text one.
   */
  public static String generateHashedPassword(String plainTextPassword) { 
    return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(LOG_ROUNDS));
  }
  
  /**
   * Checks if the plain text and hashed passwords match.
   */
  public static boolean isValidPassword(String plainTextPassword, String hashedPassword) {
    boolean isValidPassword = false;
    try {
      isValidPassword = BCrypt.checkpw(plainTextPassword, hashedPassword);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Error validating password.", e);
    }
    return isValidPassword;
  }

  /**
   * Unset the password from the identifiableUserEntity.
   * This is usually called on the Controller layer to avoid returning passwords in the APIs.
   */
  public static <P> void unsetPassword(IdentifiableUserEntity<P> entity) {
    //TODO check if there's a better generic way to do this than checking with instanceof
    if (entity != null) {
      if (entity.getPassword() instanceof byte[]) {
        entity.setPassword((P) new byte[0]);
      } else {
        entity.setPassword(null);
      }
    }
  }

  /**
   * Unset the password from the list of entities.
   */
  public static <T> void unsetPassword(List<T> entities) {
    if (entities == null) {
      return;
    }
    for (T entity : entities) {
      if (entity != null && entity instanceof IdentifiableUserEntity) {
        unsetPassword((IdentifiableUserEntity) entity);
      }
    }
  }
}
