package com.nicobrest.kamehouse.tennisworld.model;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.model.IdentifiablePasswordEntity;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * TennisWorld user to send requests to tennisworld with.
 *
 * @author nbrest
 */
@Entity
@Table(name = "tennisworld_user")
public class TennisWorldUser implements IdentifiablePasswordEntity<byte[]>, Serializable {

  private static final long serialVersionUID = 159367676076449689L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Masked
  @Column(name = "password", unique = false, nullable = false)
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

  /** Get the password, return empty byte[] if null. */
  public byte[] getPassword() {
    if (password != null) {
      return password.clone();
    } else {
      return new byte[0];
    }
  }

  /** Set the password. */
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
    return id.equals(that.id) && email.equals(that.email);
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
