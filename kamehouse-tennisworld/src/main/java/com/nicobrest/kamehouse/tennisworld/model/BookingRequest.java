package com.nicobrest.kamehouse.tennisworld.model;

import static javax.persistence.TemporalType.DATE;

import com.nicobrest.kamehouse.commons.model.IdentifiablePasswordEntity;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.tennisworld.model.dto.BookingRequestDto;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents a tennis world booking request.
 *
 * @author nbrest
 */
@Entity
@Table(name = "booking_request")
public class BookingRequest implements IdentifiablePasswordEntity<String>, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(length = 100, name = "username", unique = false, nullable = false)
  private String username;

  @Transient private String password;

  /** Format yyyy-mm-dd. */
  @Column(name = "date", unique = false, nullable = false)
  @Temporal(DATE)
  private Date date;

  /** Format: HH:MM 24hs : 07:15, 11:30, 20:15, etc. */
  @Column(length = 5, name = "time", unique = false, nullable = false)
  private String time;

  @Enumerated(EnumType.STRING)
  @Column(length = 50, name = "site", unique = false, nullable = false)
  private Site site;

  @Enumerated(EnumType.STRING)
  @Column(length = 50, name = "session_type", unique = false, nullable = false)
  private SessionType sessionType;

  /** Duration in minutes. Format: MMM (optional depending on sessionType) */
  @Column(length = 3, name = "duration", unique = false, nullable = true)
  private String duration;

  @Transient private CardDetails cardDetails;

  @Column(name = "dry_run", unique = false, nullable = false)
  private boolean dryRun = false;

  @CreationTimestamp
  @Column(name = "creation_date", unique = false, nullable = false)
  private Date creationDate = new Date();

  @Column(name = "scheduled", unique = false, nullable = false)
  private boolean scheduled = false;

  /** Convert this entity to it's dto. */
  public BookingRequestDto toDto() {
    BookingRequestDto dto = new BookingRequestDto();
    dto.setId(getId());
    dto.setCardDetails(getCardDetails());
    dto.setCreationDate(getCreationDate());
    dto.setDate(getDate());
    dto.setDryRun(isDryRun());
    dto.setDuration(getDuration());
    dto.setPassword(getPassword());
    dto.setScheduled(isScheduled());
    dto.setSessionType(getSessionType());
    dto.setSite(getSite());
    dto.setTime(getTime());
    dto.setUsername(getUsername());
    return dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  /** Get booking date. */
  public Date getDate() {
    if (date != null) {
      return (Date) date.clone();
    } else {
      return null;
    }
  }

  /** Set booking date. */
  public void setDate(Date date) {
    if (date != null) {
      this.date = (Date) date.clone();
    } else {
      this.date = null;
    }
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Site getSite() {
    return site;
  }

  public void setSite(Site site) {
    this.site = site;
  }

  public SessionType getSessionType() {
    return sessionType;
  }

  public void setSessionType(SessionType sessionType) {
    this.sessionType = sessionType;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public CardDetails getCardDetails() {
    return cardDetails;
  }

  public void setCardDetails(CardDetails cardDetails) {
    this.cardDetails = cardDetails;
  }

  public boolean isDryRun() {
    return dryRun;
  }

  public void setDryRun(boolean dryRun) {
    this.dryRun = dryRun;
  }

  /** Get date. */
  public Date getCreationDate() {
    if (creationDate != null) {
      return (Date) creationDate.clone();
    } else {
      return null;
    }
  }

  /** Set date. */
  public void setCreationDate(Date creationDate) {
    if (creationDate != null) {
      this.creationDate = (Date) creationDate.clone();
    } else {
      this.creationDate = null;
    }
  }

  public boolean isScheduled() {
    return scheduled;
  }

  public void setScheduled(boolean scheduled) {
    this.scheduled = scheduled;
  }

  @Override
  public int hashCode() {
    String dateFormatted = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, date);
    return new HashCodeBuilder()
        .append(id)
        .append(username)
        .append(password)
        .append(dateFormatted)
        .append(time)
        .append(site)
        .append(sessionType)
        .append(scheduled)
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof BookingRequest) {
      final BookingRequest other = (BookingRequest) obj;
      String dateFormatted = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, date);
      String otherDateFormatted = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, other.getDate());
      return new EqualsBuilder()
          .append(id, other.getId())
          .append(username, other.getUsername())
          .append(password, other.getPassword())
          .append(dateFormatted, otherDateFormatted)
          .append(time, other.getTime())
          .append(site, other.getSite())
          .append(sessionType, other.getSessionType())
          .append(scheduled, other.isScheduled())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    String[] maskedFields = {"password", "cardDetails.number", "cardDetails.cvv"};
    return JsonUtils.toJsonString(this, super.toString(), maskedFields);
  }

  /** Card details to complete the payment. */
  public static class CardDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String number;
    private String expiryDate; // Format: MM/YYYY
    private String cvv;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getNumber() {
      return number;
    }

    public void setNumber(String number) {
      this.number = number;
    }

    public String getExpiryDate() {
      return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
      this.expiryDate = expiryDate;
    }

    public String getCvv() {
      return cvv;
    }

    public void setCvv(String cvv) {
      this.cvv = cvv;
    }

    /** Calculate the card expiry month. */
    public String calculateExpiryMonth() {
      if (expiryDate != null && expiryDate.length() == 7) {
        return expiryDate.substring(0, 2);
      } else {
        return null;
      }
    }

    /** Calculate the card expiry year. */
    public String calculateExpiryYear() {
      if (expiryDate != null && expiryDate.length() == 7) {
        return expiryDate.substring(3, 7);
      } else {
        return null;
      }
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder().append(name).append(number).append(expiryDate).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof CardDetails) {
        final CardDetails other = (CardDetails) obj;
        return new EqualsBuilder()
            .append(name, other.getName())
            .append(number, other.getNumber())
            .append(expiryDate, other.getExpiryDate())
            .isEquals();
      } else {
        return false;
      }
    }

    @Override
    public String toString() {
      String[] maskedFields = {"number", "cvv"};
      return JsonUtils.toJsonString(this, super.toString(), maskedFields);
    }
  }
}
