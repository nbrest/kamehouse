package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Get court weekly schedule request to tennisworld.
 *
 * @author nbrest
 */
public class CourtWeeklyScheduleRequest implements RequestBody {

  private long clubId;
  private String date;
  private Long zoneTypeId;
  private Long zoneId;
  private String slots;
  private Integer daysInWeek;

  public long getClubId() {
    return clubId;
  }

  public void setClubId(long clubId) {
    this.clubId = clubId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Long getZoneTypeId() {
    return zoneTypeId;
  }

  public void setZoneTypeId(Long zoneTypeId) {
    this.zoneTypeId = zoneTypeId;
  }

  public String getSlots() {
    return slots;
  }

  public void setSlots(String slots) {
    this.slots = slots;
  }

  public Integer getDaysInWeek() {
    return daysInWeek;
  }

  public void setDaysInWeek(Integer daysInWeek) {
    this.daysInWeek = daysInWeek;
  }

  public Long getZoneId() {
    return zoneId;
  }

  public void setZoneId(Long zoneId) {
    this.zoneId = zoneId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    CourtWeeklyScheduleRequest that = (CourtWeeklyScheduleRequest) other;
    return Objects.equals(clubId, that.clubId)
        && Objects.equals(date, that.date)
        && Objects.equals(zoneTypeId, that.zoneTypeId)
        && Objects.equals(slots, that.slots)
        && Objects.equals(daysInWeek, that.daysInWeek)
        && Objects.equals(zoneId, that.zoneId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clubId, date, zoneTypeId, slots, daysInWeek, zoneId);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
