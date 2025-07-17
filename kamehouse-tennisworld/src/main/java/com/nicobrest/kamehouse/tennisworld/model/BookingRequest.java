package com.nicobrest.kamehouse.tennisworld.model;

import static jakarta.persistence.TemporalType.DATE;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.model.PasswordEntity;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
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
public class BookingRequest implements PasswordEntity<String>, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(length = 100, name = "username", unique = false, nullable = false)
  private String username;

  @Masked
  @Transient
  private String password;

  /**
   * Format yyyy-mm-dd.
   */
  @Column(name = "date", unique = false, nullable = false)
  @Temporal(DATE)
  private Date date;

  /**
   * Format: HH:MM 24hs : 07:15, 11:30, 20:15, etc.
   */
  @Column(length = 5, name = "time", unique = false, nullable = false)
  private String time;

  @Enumerated(EnumType.STRING)
  @Column(length = 50, name = "site", unique = false, nullable = false)
  private Site site;

  @Enumerated(EnumType.STRING)
  @Column(length = 50, name = "session_type", unique = false, nullable = false)
  private SessionType sessionType;

  /**
   * Duration in minutes. Format: MMM (optional depending on sessionType)
   */
  @Column(length = 3, name = "duration", unique = false, nullable = true)
  private String duration;

  @Transient
  private CardDetails cardDetails;

  @Column(name = "dry_run", unique = false, nullable = false)
  private boolean dryRun = false;

  @CreationTimestamp
  @Column(name = "creation_date", unique = false, nullable = false)
  private Date creationDate = DateUtils.getCurrentDate();

  @Column(name = "scheduled", unique = false, nullable = false)
  private boolean scheduled = false;

  @Column(name = "court_number", unique = false, nullable = true)
  private Integer courtNumber = 0;

  @Column(name = "retries", unique = false, nullable = true)
  private Integer retries;

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

  /**
   * Get booking date.
   */
  public Date getDate() {
    if (date != null) {
      return (Date) date.clone();
    } else {
      return null;
    }
  }

  /**
   * Set booking date.
   */
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

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public CardDetails getCardDetails() {
    return cardDetails;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setCardDetails(CardDetails cardDetails) {
    this.cardDetails = cardDetails;
  }

  public boolean isDryRun() {
    return dryRun;
  }

  public void setDryRun(boolean dryRun) {
    this.dryRun = dryRun;
  }

  /**
   * Get date.
   */
  public Date getCreationDate() {
    if (creationDate != null) {
      return (Date) creationDate.clone();
    } else {
      return null;
    }
  }

  /**
   * Set date.
   */
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

  public Integer getCourtNumber() {
    return courtNumber;
  }

  public void setCourtNumber(Integer courtNumber) {
    this.courtNumber = courtNumber;
  }

  public Integer getRetries() {
    return retries;
  }

  public void setRetries(Integer retries) {
    this.retries = retries;
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
        .append(courtNumber)
        .append(retries)
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof BookingRequest other) {
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
          .append(courtNumber, other.getCourtNumber())
          .append(retries, other.getRetries())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }

  /**
   * Card details to complete the payment.
   */
  public static class CardDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    @Masked
    private String number;
    private String expiryDate; // Format: MM/YYYY
    @Masked
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

    /**
     * Calculate the card expiry month.
     */
    public String calculateExpiryMonth() {
      if (expiryDate != null && expiryDate.length() == 7) {
        return expiryDate.substring(0, 2);
      } else {
        return null;
      }
    }

    /**
     * Calculate the card expiry year.
     */
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
      if (obj instanceof CardDetails other) {
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
      return JsonUtils.toJsonString(this, super.toString());
    }
  }
}
