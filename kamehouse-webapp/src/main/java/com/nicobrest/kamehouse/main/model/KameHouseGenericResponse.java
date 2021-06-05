package com.nicobrest.kamehouse.main.model;

import com.nicobrest.kamehouse.main.utils.JsonUtils;

import java.util.Objects;

/**
 * Generic response to return by APIs when only a simple message needs to be returned.
 *
 * @author nbrest
 *
 */
public class KameHouseGenericResponse {

  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    KameHouseGenericResponse that = (KameHouseGenericResponse) obj;
    return Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
