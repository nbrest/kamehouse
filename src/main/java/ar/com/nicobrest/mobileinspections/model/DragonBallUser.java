package ar.com.nicobrest.mobileinspections.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *        DragonBallUser POJO used for the test endpoints.
 *         
 * @author nbrest
 */
@Entity
@Table(name = "dragonballuser")
public class DragonBallUser {

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  
  @Column(name = "username", unique = true, nullable = false)
  private String username;
  
  @Column(name = "email")
  private String email;
  
  @Column(name = "age")
  private int age;
  
  @Column(name = "powerlevel")
  private int powerLevel;
  
  @Column(name = "stamina")
  private int stamina;

  /**
   *      Constructor.
   *      
   * @author nbrest 
   */
  public DragonBallUser() {}
  
  /**
   *      Constructor.
   *      
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
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public void setAge(int age) {
    this.age = age;
  }

  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public void setPowerLevel(int powerLevel) {
    this.powerLevel = powerLevel;
  }
  
  /**
   *      Getters and Setters.
   * 
   * @author nbrest
   */
  public void setStamina(int stamina) {
    this.stamina = stamina;
  }
 
  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public Long getId() {
    return id;
  }  
  
  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public String getUsername() {
    return username;
  }

  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public String getEmail() {
    return email;
  }

  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public int getAge() {
    return age;
  }
 
  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public int getPowerLevel() {
    return powerLevel;
  }
  
  /**
   *      Getters and Setters.
   *      
   * @author nbrest
   */
  public int getStamina() {
    return stamina;
  }
  
  
  /**
   *      Attack another DragonBallUser.
   *      
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
   *      Recover stamina.
   *      
   * @author nbrest 
   */
  public void recoverStamina() {
    
    stamina = stamina + powerLevel;
  }  
  
  /**
   *      Hashcode.
   * 
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
   *      Equals.
   * 
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