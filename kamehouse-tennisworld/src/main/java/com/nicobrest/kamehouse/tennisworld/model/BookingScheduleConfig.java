package com.nicobrest.kamehouse.tennisworld.model;

import static javax.persistence.TemporalType.DATE;

import com.nicobrest.kamehouse.commons.dao.Identifiable;
import com.nicobrest.kamehouse.commons.utils.DateUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;

import org.hibernate.annotations.ColumnDefault;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;

/**
 * BookingScheduleConfig defines a configuration to execute a scheduled booking.
 * 
 * @author nbrest
 */
@Entity
@Table(name = "booking_schedule_config", uniqueConstraints = @UniqueConstraint(
    columnNames = {"tennisworld_user_id", "session_type", "site",
        "day", "time", "booking_date", "book_ahead_days" }))
public class BookingScheduleConfig implements Identifiable, Serializable {

  private static final long serialVersionUID = 159367676076449689L;

  @Id
  @Column(name = "id", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "tennisworld_user_id", referencedColumnName = "id")
  private TennisWorldUser tennisWorldUser;

  @Enumerated(EnumType.STRING)
  @Column(length = 50, name = "session_type", unique = false, nullable = false)
  private TennisWorldSessionType sessionType;

  @Enumerated(EnumType.STRING)
  @Column(length = 50, name = "site", unique = false, nullable = false)
  private TennisWorldSite site;

  @Enumerated(EnumType.STRING)
  @Column(length = 10, name = "day", unique = false, nullable = false)
  private DateUtils.Day day;

  /**
   * Format: HH:MM[am|pm] 07:15pm, 11:30am, 12:00pm, etc.
   */
  @Column(length = 7, name = "time", unique = false, nullable = false)
  private String time;

  /**
   * Format: 'yyyy-mm-dd'. Passing 'yyyy/mm/dd' sets the date as null and then to its default value.
   * If specified, the booking will only happen for the specified date (yyyy-mm-dd).
   * This configuration will not be activated for other dates even if it matches all the
   * other criteria.
   * This field is to allow the possibility of one-off scheduled bookings for a specific date.
   * If this is set, the day property will be ignored.
   * The time will still be taken from the time property.
   * If bookingDate is '1984-10-15', this configuration will be activated recurrently for
   * the specified day and time.
   * I had to add the default value of '1984-10-15' instead of null, otherwise the
   * @UniqueConstraint defined above doesn't get picked up in mysql.
   */
  @Column(name = "booking_date", unique = false, nullable = false)
  @ColumnDefault(value = "'1984-10-15'")
  @Temporal(DATE)
  private Date bookingDate;

  /**
   * Number of days ahead to execute the booking (For cardio sessions should be 14).
   * The schedule booking job runs every day checking for executable booking configurations.
   * If for example, today is MONDAY, and there is a schedule configuration that states
   * { day: WEDNESDAY, book_ahead_days: 2 }
   * it's going to trigger the booking today (monday), to book the session for wednesday.
   * This field should accept 0+ values.
   */
  @Column(name = "book_ahead_days", unique = false, nullable = false)
  private Integer bookAheadDays;

  @Column(name = "enabled", unique = false, nullable = false)
  private Boolean enabled;

  /**
   * Duration in minutes. Format: MM (optional depending on sessionType)
   */
  @Column(length = 3, name = "duration", unique = false, nullable = true)
  private String duration;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public TennisWorldUser getTennisWorldUser() {
    return tennisWorldUser;
  }

  public void setTennisWorldUser(TennisWorldUser tennisWorldUser) {
    this.tennisWorldUser = tennisWorldUser;
  }

  public TennisWorldSessionType getSessionType() {
    return sessionType;
  }

  public void setSessionType(TennisWorldSessionType sessionType) {
    this.sessionType = sessionType;
  }

  public TennisWorldSite getSite() {
    return site;
  }

  public void setSite(TennisWorldSite site) {
    this.site = site;
  }

  public DateUtils.Day getDay() {
    return day;
  }

  public void setDay(DateUtils.Day day) {
    this.day = day;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  /**
   * Get booking date.
   */
  public Date getBookingDate() {
    if (bookingDate != null) {
      return (Date) bookingDate.clone();
    } else {
      return null;
    }
  }

  /**
   * Set booking date.
   */
  public void setBookingDate(Date bookingDate) {
    if (bookingDate != null) {
      this.bookingDate = (Date) bookingDate.clone();
    } else {
      this.bookingDate = null;
    }

  }

  public Integer getBookAheadDays() {
    return bookAheadDays;
  }

  public void setBookAheadDays(Integer bookAheadDays) {
    this.bookAheadDays = bookAheadDays;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    BookingScheduleConfig that = (BookingScheduleConfig) other;
    String bookingDateFormatted = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, bookingDate);
    String otherBookingDateFormatted = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD,
        that.getBookingDate());
    return Objects.equals(id, that.id)
        && Objects.equals(tennisWorldUser, that.tennisWorldUser)
        && sessionType == that.sessionType
        && site == that.site
        && day == that.day
        && Objects.equals(time, that.time)
        && Objects.equals(bookingDateFormatted, otherBookingDateFormatted)
        && Objects.equals(bookAheadDays, that.bookAheadDays);
  }

  @Override
  public int hashCode() {
    String bookingDateFormatted = DateUtils.getFormattedDate(DateUtils.YYYY_MM_DD, bookingDate);
    return Objects.hash(id, tennisWorldUser, sessionType, site, day, time, bookingDateFormatted,
        bookAheadDays);
  }

  @Override
  public String toString() {
    String[] maskedFields = { "tennisWorldUser.password" };
    return JsonUtils.toJsonString(this, super.toString(), maskedFields);
  }
}