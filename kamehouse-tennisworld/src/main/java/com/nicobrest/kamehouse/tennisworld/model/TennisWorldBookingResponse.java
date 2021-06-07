package com.nicobrest.kamehouse.tennisworld.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Response containing the final status for a tennis world booking request.
 *
 * @author nbrest
 */
public class TennisWorldBookingResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private Status status;
  private String message;

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    TennisWorldBookingResponse that = (TennisWorldBookingResponse) other;
    return Objects.equals(status, that.status) && Objects.equals(message, that.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }

  /**
   * Final status of the tennis world booking request.
   */
  public enum Status {
    SUCCESS,
    ERROR,
    INTERNAL_ERROR
  }
}
