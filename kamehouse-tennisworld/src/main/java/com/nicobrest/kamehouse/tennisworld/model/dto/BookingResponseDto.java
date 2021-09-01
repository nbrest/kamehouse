package com.nicobrest.kamehouse.tennisworld.model.dto;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.BookingResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Objects;

/**
 * Response containing the final status for a tennis world booking request.
 *
 * @author nbrest
 */
public class BookingResponseDto implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private BookingResponse.Status status;
  private String message;
  private BookingRequest request;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BookingResponse.Status getStatus() {
    return status;
  }

  public void setStatus(BookingResponse.Status status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public BookingRequest getRequest() {
    return request;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setRequest(BookingRequest request) {
    this.request = request;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    BookingResponseDto that = (BookingResponseDto) other;
    return status == that.status
        && Objects.equals(id, that.id)
        && Objects.equals(message, that.message)
        && Objects.equals(request, that.request);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status, message, request);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
