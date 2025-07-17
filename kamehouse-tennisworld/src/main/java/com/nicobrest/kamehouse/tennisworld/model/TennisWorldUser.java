package com.nicobrest.kamehouse.tennisworld.model;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.model.PasswordEntity;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * TennisWorld user to send requests to tennisworld with.
 *
 * @author nbrest
 */
@Entity
@Table(name = "tennisworld_user")
public class TennisWorldUser implements Identifiable, PasswordEntity<byte[]>, Serializable {

  private static final long serialVersionUID = 159367676076449689L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(length = 50, name = "email", unique = true, nullable = false)
  private String email;

  @Masked
  @Column(name = "password", unique = false, nullable = false, columnDefinition = "BLOB")
  @Lob
  private byte[] password;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Get the password, return empty byte[] if null.
   */
  public byte[] getPassword() {
    if (password != null) {
      return password.clone();
    } else {
      return new byte[0];
    }
  }

  /**
   * Set the password.
   */
  public void setPassword(byte[] password) {
    if (password != null) {
      this.password = password.clone();
    }
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    TennisWorldUser that = (TennisWorldUser) other;
    return Objects.equals(id, that.id)
        && Objects.equals(email, that.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
