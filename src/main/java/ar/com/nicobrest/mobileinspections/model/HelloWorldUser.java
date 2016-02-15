package ar.com.nicobrest.mobileinspections.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @since v0.02 
 * @author nbrest
 *
 *         HelloWorldUser POJO used for the test endpoints
 */
public class HelloWorldUser {

  String username;
  String email;
  int age;

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
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder()
                .append(username)
                .append(email)
                .append(age)
                .toHashCode();
  }

  /**
   * @since v0.03 
   * @author nbrest
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof HelloWorldUser) {
      final HelloWorldUser other = (HelloWorldUser) obj;
      return new EqualsBuilder()
                .append(username, other.getUsername())
                .append(email, other.getEmail())
                .append(age, other.getAge())
                .isEquals();
    } else {
      return false;
    }
  }
}