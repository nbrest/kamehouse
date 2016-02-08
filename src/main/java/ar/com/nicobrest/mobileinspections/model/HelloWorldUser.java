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

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public int getAge() {
    return age;
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder()
                .append(username)
                .append(email)
                .append(age)
                .toHashCode();
  }

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