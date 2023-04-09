package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Set booking details request to tennisworld.
 *
 * @author nbrest
 */
public class SetCourtBookingDetailsRequest implements RequestBody {

  @JsonProperty("UserId")
  private long userId;

  @JsonProperty("StartTime")
  private String startTime;

  @JsonProperty("ZoneId")
  private Long zoneId;

  @JsonProperty("RequiredNumberOfSlots")
  private String requiredNumberOfSlots;

  @JsonProperty("Duration")
  private int duration;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getRequiredNumberOfSlots() {
    return requiredNumberOfSlots;
  }

  public void setRequiredNumberOfSlots(String requiredNumberOfSlots) {
    this.requiredNumberOfSlots = requiredNumberOfSlots;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
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
    SetCourtBookingDetailsRequest that = (SetCourtBookingDetailsRequest) other;
    return Objects.equals(userId, that.userId)
        && Objects.equals(startTime, that.startTime)
        && Objects.equals(requiredNumberOfSlots, that.requiredNumberOfSlots)
        && Objects.equals(duration, that.duration)
        && Objects.equals(zoneId, that.zoneId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, startTime, requiredNumberOfSlots, duration, zoneId);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
