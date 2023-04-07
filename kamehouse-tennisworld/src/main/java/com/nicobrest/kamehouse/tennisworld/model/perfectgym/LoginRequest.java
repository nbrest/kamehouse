package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Login request to tennisworld.
 *
 * @author nbrest
 */
public class LoginRequest implements RequestBody {

  @JsonProperty("RememberMe")
  private boolean rememberMe;

  @JsonProperty("Login")
  private String username;

  @JsonProperty("Password")
  @Masked
  private String password;

  public boolean isRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(boolean rememberMe) {
    this.rememberMe = rememberMe;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    LoginRequest that = (LoginRequest) other;
    return Objects.equals(rememberMe, that.rememberMe)
        && Objects.equals(username, that.username)
        && Objects.equals(password, that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rememberMe, username, password);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
