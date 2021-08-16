package com.nicobrest.kamehouse.tennisworld.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Represents a tennis world booking request.
 * 
 * @author nbrest
 *
 */
public class BookingRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String username;
  private String password;
  private String date; // Format: YYYY-MM-DD
  private String time; // Format: HH:MMam or HH:MMpm
  private String site;
  private String sessionType;
  private String duration; // Format: MM (optional depending on sessionType)
  private CardDetails cardDetails;
  private boolean dryRun = false;

  public String getId() {
    return id;
  }

  public void setId(String id) {
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

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getSessionType() {
    return sessionType;
  }

  public void setSessionType(String sessionType) {
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
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof BookingRequest) {
      final BookingRequest other = (BookingRequest) obj;
      return new EqualsBuilder()
          .append(id, other.getId())
          .append(username, other.getUsername())
          .append(password,other.getPassword())
          .append(date,other.getDate())
          .append(time,other.getTime())
          .append(site,other.getSite())
          .append(sessionType,other.getSessionType())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    String[] maskedFields = { "password", "cardDetails.number", "cardDetails.cvv" };
    return JsonUtils.toJsonString(this, super.toString(), maskedFields);
  }

  /**
   * Card details to complete the payment.
   */
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

    /**
     * Get the card expiry month.
     */
    public String getExpiryMonth() {
      if (expiryDate != null && expiryDate.length() == 7) {
        return expiryDate.substring(0,2);
      } else {
        return null;
      }
    }

    /**
     * Get the card expiry year.
     */
    public String getExpiryYear() {
      if (expiryDate != null && expiryDate.length() == 7) {
        return expiryDate.substring(3,7);
      } else {
        return null;
      }
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder()
          .append(name)
          .append(number)
          .append(expiryDate)
          .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof CardDetails) {
        final CardDetails other = (CardDetails) obj;
        return new EqualsBuilder()
            .append(name, other.getName())
            .append(number,other.getNumber())
            .append(expiryDate,other.getExpiryDate())
            .isEquals();
      } else {
        return false;
      }
    }

    @Override
    public String toString() {
      String[] maskedFields = { "number", "cvv" };
      return JsonUtils.toJsonString(this, super.toString(), maskedFields);
    }
  }
}
