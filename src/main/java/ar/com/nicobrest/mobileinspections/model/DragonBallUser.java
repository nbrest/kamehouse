package ar.com.nicobrest.mobileinspections.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @since v0.02 
 * @author nbrest
 *
 *         DragonBallUser POJO used for the test endpoints
 */
public class DragonBallUser {

  private Long id;
  private String username;
  private String email;
  private int age;
  private int powerLevel;
  private int stamina;

  /**
   * @since v0.03 
   * @author nbrest
   * @param id
   * 
   *      Getters and Setters
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * @since v0.02 
   * @author nbrest
   * @param username
   * 
   *      Getters and Setters
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @since v0.02 
   * @author nbrest
   * @param email
   * 
   *      Getters and Setters
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @since v0.02 
   * @author nbrest
   * @param age
   * 
   *      Getters and Setters
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   * @since v0.03 
   * @author nbrest
   * @param powerLevel
   * 
   *      Getters and Setters
   */
  public void setPowerLevel(int powerLevel) {
    this.powerLevel = powerLevel;
  }
  
  /**
   * @since v0.03 
   * @author nbrest
   * @param stamina
   * 
   *      Getters and Setters
   */
  public void setStamina(int stamina) {
    this.stamina = stamina;
  }
 
  /**
   * @since v0.03 
   * @author nbrest
   * @return Long
   * 
   *      Getters and Setters
   */
  public Long getId() {
    return id;
  }  
  
  /**
   * @since v0.02 
   * @author nbrest
   * @return String
   * 
   *      Getters and Setters
   */
  public String getUsername() {
    return username;
  }

  /**
   * @since v0.02 
   * @author nbrest
   * @return String
   * 
   *      Getters and Setters
   */
  public String getEmail() {
    return email;
  }

  /**
   * @since v0.02 
   * @author nbrest
   * @return int
   * 
   *      Getters and Setters
   */
  public int getAge() {
    return age;
  }
 
  /**
   * @since v0.03 
   * @author nbrest
   * @return int
   * 
   *      Getters and Setters
   */
  public int getPowerLevel() {
    return powerLevel;
  }
  
  /**
   * @since v0.03 
   * @author nbrest
   * @return int
   * 
   *      Getters and Setters
   */
  public int getStamina() {
    return stamina;
  }
  
  
  /**
   * @since v0.03 
   * @author nbrest 
   * 
   *      Attack another DragonBallUser
   */
  public void attack(DragonBallUser opponent) {
    
    /* Check for nulls in parameters in methods that can be called from
     * outside the application, where I don´t know what the client can send 
    if (opponent == null) {
      throw new IllegalArgumentException();
    }
    */
    int currentOpponentStamina = opponent.getStamina();
    currentOpponentStamina = currentOpponentStamina - powerLevel;
    if (currentOpponentStamina < 0) {
      currentOpponentStamina = 0;
    }
    opponent.setStamina(currentOpponentStamina);
  }
  
  /**
   * @since v0.03 
   * @author nbrest 
   * 
   *      Recover stamina
   */
  public void recoverStamina() {
    
    stamina = stamina + powerLevel;
  }  
  
  /**
   * @since v0.03 
   * @author nbrest
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder()
              .append(id)
              .append(username)
              .append(email)
              .append(age)
              .append(powerLevel)
              .append(stamina)
              .toHashCode();
  }

  /**
   * @since v0.03 
   * @author nbrest
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof DragonBallUser) {
      final DragonBallUser other = (DragonBallUser) obj;
      return new EqualsBuilder()
                .append(id, other.getId())
                .append(username, other.getUsername())
                .append(email, other.getEmail())
                .append(age, other.getAge())
                .append(powerLevel, other.getPowerLevel())
                .append(stamina, other.getStamina())
                .isEquals();
    } else {
      return false;
    }
  }
}