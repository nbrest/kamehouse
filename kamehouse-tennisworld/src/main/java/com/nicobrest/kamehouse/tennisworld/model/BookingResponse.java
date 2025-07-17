package com.nicobrest.kamehouse.tennisworld.model;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

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
