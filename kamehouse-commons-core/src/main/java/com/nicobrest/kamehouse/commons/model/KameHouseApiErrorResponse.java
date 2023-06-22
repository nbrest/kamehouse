package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Error response to return by kamehouse APIs.
 *
 * @author nbrest
 */
public class KameHouseApiErrorResponse {

  private int code;
  private String message;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

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
    KameHouseApiErrorResponse that = (KameHouseApiErrorResponse) obj;
    return new EqualsBuilder()
        .append(code, that.getCode())
        .append(message, that.getMessage())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(code).append(message).toHashCode();
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
