package com.nicobrest.kamehouse.tennisworld.model;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingResponseDto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Response containing the final status for a tennis world booking request.
 *
 * @author nbrest
 */
@Entity
@Table(name = "booking_response")
public class BookingResponse implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(length = 50, name = "status", unique = false, nullable = false)
  private Status status;

  @Column(name = "message", unique = false, nullable = false)
  private String message;

  @OneToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "booking_request_id", referencedColumnName = "id")
  private BookingRequest request;

  /**
   * Convert this entity to it's dto.
   */
  public BookingResponseDto toDto() {
    BookingResponseDto dto = new BookingResponseDto();
    dto.setId(getId());
    dto.setStatus(getStatus());
    dto.setMessage(getMessage());
    dto.setRequest(getRequest());
    return dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public BookingRequest getRequest() {
    return request;
  }

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
    BookingResponse that = (BookingResponse) other;
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

  /**
   * Final status of the tennis world booking request.
   */
  public enum Status {
    SUCCESS,
    ERROR,
    INTERNAL_ERROR
  }
}
