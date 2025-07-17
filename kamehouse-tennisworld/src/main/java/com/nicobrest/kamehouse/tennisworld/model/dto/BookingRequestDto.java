package com.nicobrest.kamehouse.tennisworld.model.dto;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.tennisworld.model.BookingRequest;
import com.nicobrest.kamehouse.tennisworld.model.SessionType;
import com.nicobrest.kamehouse.tennisworld.model.Site;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a tennis world booking request dto.
 *
 * @author nbrest
 */
public class BookingRequestDto implements Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  @Masked
  private String password;
  private Date date;
  private String time;
  private Site site;
  private SessionType sessionType;
  private String duration;
  private BookingRequest.CardDetails cardDetails;
  private boolean dryRun = false;
  private Date creationDate = new Date();
  private boolean scheduled = false;
  private Integer courtNumber = 0;
  private Integer retries;

  @Override
  public Long getId() {
    return id;
  }

  @Override
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
  public BookingRequest.CardDetails getCardDetails() {
    return cardDetails;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public void setCardDetails(BookingRequest.CardDetails cardDetails) {
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
    return new HashCodeBuilder()
        .append(id)
        .append(username)
        .append(password)
        .append(date)
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
    if (obj instanceof BookingRequestDto other) {
      return new EqualsBuilder()
          .append(id, other.getId())
          .append(username, other.getUsername())
          .append(password, other.getPassword())
          .append(date, other.getDate())
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
}
