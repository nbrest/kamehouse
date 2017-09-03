package com.nicobrest.kamehouse.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicobrest.kamehouse.exception.KameHouseInvalidDataException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DragonBallUser used for the test endpoints.
 * 
 * @author nbrest
 */
@Entity
@Table(name = "dragonballuser")
public class DragonBallUser implements Serializable {

  private static final int MAX_STRING_LENGTH = 255;
  private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
      + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private static final String USERNAME_PATTERN = "^[A-Za-z0-9]+[\\._A-Za-z0-9-]*";

  private static final long serialVersionUID = 159367676076449689L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "age")
  private int age;

  @Column(name = "powerlevel")
  private int powerLevel;

  @Column(name = "stamina")
  private int stamina;

  /**
   * Constructor.
   * 
   * @author nbrest
   */
  public DragonBallUser() {

  }

  /**
   * Constructor.
   * 
   * @author nbrest
   */
  public DragonBallUser(Long id, String username, String email, int age, int powerLevel,
      int stamina) {

    this.id = id;
    this.username = username;
    this.email = email;
    this.age = age;
    this.powerLevel = powerLevel;
    this.stamina = stamina;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public void setPowerLevel(int powerLevel) {
    this.powerLevel = powerLevel;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public void setStamina(int stamina) {
    this.stamina = stamina;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public Long getId() {
    return id;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public String getUsername() {
    return username;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public String getEmail() {
    return email;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public int getAge() {
    return age;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public int getPowerLevel() {
    return powerLevel;
  }

  /**
   * Getters and Setters.
   * 
   * @author nbrest
   */
  public int getStamina() {
    return stamina;
  }

  /**
   * Attack another DragonBallUser.
   * 
   * @author nbrest
   */
  public void attack(DragonBallUser opponent) {

    /*
     * Check for nulls in parameters in methods that can be called from outside
     * the application, where I don´t know what the client can send if (opponent
     * == null) { throw new IllegalArgumentException(); }
     */
    int currentOpponentStamina = opponent.getStamina();
    currentOpponentStamina = currentOpponentStamina - powerLevel;
    if (currentOpponentStamina < 0) {
      currentOpponentStamina = 0;
    }
    opponent.setStamina(currentOpponentStamina);
  }

  /**
   * Recover stamina.
   * 
   * @author nbrest
   */
  public void recoverStamina() {

    stamina = stamina + powerLevel;
  }

  /**
   * Performs all the input and logical validations on a DragonBallUser and
   * throw an exception if a validation fails.
   * 
   * @author nbrest
   */
  public void validateAllFields() {

    /*
     * Adding these validation methods to the setter and getters caused some
     * problems with autowiring with Spring so instead of that, call this method
     * to validate the fields before persisting the object to the database.
     * 
     * - username must contain lettes, numbers, dots, '-' or '_'. And start with
     * a letter or numberf - check valid format in the email field:
     * sth1@sth2.sth3 - age and powerlevel should be > 0 - strings shouldn´t be
     * longer than the supported 255 characters of varchar in the database
     */
    validateUsernameFormat(username);
    validateStringLength(username);

    validateEmailFormat(email);
    validateStringLength(email);

    validatePositiveValue(age);

    validatePositiveValue(powerLevel);
  }

  /**
   * Validate that the username respects the established format.
   * 
   * @author nbrest
   */
  private void validateUsernameFormat(String username) {

    Pattern pattern = Pattern.compile(USERNAME_PATTERN);
    Matcher matcher = pattern.matcher(username);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid username format: " + username);
    }
  }

  /**
   * Validate that the email has a valid format.
   * 
   * @author nbrest
   */
  private void validateEmailFormat(String email) {

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    if (!matcher.matches()) {
      throw new KameHouseInvalidDataException("Invalid email address: " + email);
    }
  }

  /**
   * Validate that the integer has a positive value.
   * 
   * @author nbrest
   */
  private void validatePositiveValue(int value) {

    if (value < 0) {
      throw new KameHouseInvalidDataException(
          "The attribute should be a positive value. Current value: " + value);
    }
  }

  /**
   * Validate that the string lenght is accepted by the database.
   * 
   * @author nbrest
   */
  private void validateStringLength(String value) {

    if (value.length() > MAX_STRING_LENGTH) {
      throw new KameHouseInvalidDataException("The string attribute excedes the maximum length of "
          + MAX_STRING_LENGTH + ". Current length: " + value.length());
    }
  }

  /**
   * Hashcode.
   * 
   * @author nbrest
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).append(username).toHashCode();
  }

  /**
   * Equals.
   * 
   * @author nbrest
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof DragonBallUser) {
      final DragonBallUser other = (DragonBallUser) obj;
      return new EqualsBuilder().append(id, other.getId()).append(username, other.getUsername())
          .isEquals();
    } else {
      return false;
    }
  }

  /**
   * toString as json representation.
   * 
   * @author nbrest
   */
  @Override
  public String toString() {

    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      e.printStackTrace(); 
    }
    return "DragonBallUser: INVALID_STATE";
  }
}