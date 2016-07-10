package ar.com.nicobrest.mobileinspections.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *        DragonBallUser POJO used for the test endpoints
 *         
 * @since v0.02 
 * @author nbrest
 */
public class DragonBallUser {

  private Long id;
  private String username;
  private String email;
  private int age;
  private int powerLevel;
  private int stamina;

  /**
   *      Constructor
   *      
   * @since v0.03
   * @author nbrest 
   */
  public DragonBallUser() {}
  
  /**
   *      Constructor
   *      
   * @since v0.03
   * @author nbrest
   * @param id : User id
   * @param username : User unique name
   * @param email : User email address
   * @param age : user age
   * @param powerLevel : User power level
   * @param stamina : User stamina
   */
  public DragonBallUser(Long id, String username, String email, int age, 
      int powerLevel, int stamina) {
    
    this.id = id;
    this.username = username;
    this.email = email;
    this.age = age;
    this.powerLevel = powerLevel;
    this.stamina = stamina;
  }
  
  /**
   *      Getters and Setters
   *      
   * @since v0.03 
   * @author nbrest
   * @param id : User id
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   *      Getters and Setters
   *      
   * @since v0.02 
   * @author nbrest
   * @param username : User name
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   *      Getters and Setters
   *      
   * @since v0.02 
   * @author nbrest
   * @param email : User email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   *      Getters and Setters
   *      
   * @since v0.02 
   * @author nbrest
   * @param age : User age
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   *      Getters and Setters
   *      
   * @since v0.03 
   * @author nbrest
   * @param powerLevel : User power level
   */
  public void setPowerLevel(int powerLevel) {
    this.powerLevel = powerLevel;
  }
  
  /**
   *      Getters and Setters
   * 
   * @since v0.03 
   * @author nbrest
   * @param stamina : User stamina
   */
  public void setStamina(int stamina) {
    this.stamina = stamina;
  }
 
  /**
   *      Getters and Setters
   *      
   * @since v0.03 
   * @author nbrest
   * @return Long
   */
  public Long getId() {
    return id;
  }  
  
  /**
   *      Getters and Setters
   *      
   * @since v0.02 
   * @author nbrest
   * @return String
   */
  public String getUsername() {
    return username;
  }

  /**
   *      Getters and Setters
   *      
   * @since v0.02 
   * @author nbrest
   * @return String
   */
  public String getEmail() {
    return email;
  }

  /**
   *      Getters and Setters
   *      
   * @since v0.02 
   * @author nbrest
   * @return int
   */
  public int getAge() {
    return age;
  }
 
  /**
   *      Getters and Setters
   *      
   * @since v0.03 
   * @author nbrest
   * @return int
   */
  public int getPowerLevel() {
    return powerLevel;
  }
  
  /**
   *      Getters and Setters
   *      
   * @since v0.03 
   * @author nbrest
   * @return int
   */
  public int getStamina() {
    return stamina;
  }
  
  
  /**
   *      Attack another DragonBallUser
   *      
   * @since v0.03 
   * @author nbrest 
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
   *      Recover stamina
   *      
   * @since v0.03 
   * @author nbrest 
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
                .isEquals();
    } else {
      return false;
    }
  }
}