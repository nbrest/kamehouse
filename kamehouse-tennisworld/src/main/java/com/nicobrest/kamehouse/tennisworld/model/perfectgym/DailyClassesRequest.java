package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Get daily classes request to tennisworld.
 *
 * @author nbrest
 */
public class DailyClassesRequest implements RequestBody {

  private long clubId;
  private String date;
  private Long categoryId;
  private Long timeTableId;
  private Long trainerId;
  private Long zoneId;

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

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public Long getTimeTableId() {
    return timeTableId;
  }

  public void setTimeTableId(Long timeTableId) {
    this.timeTableId = timeTableId;
  }

  public Long getTrainerId() {
    return trainerId;
  }

  public void setTrainerId(Long trainerId) {
    this.trainerId = trainerId;
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
    DailyClassesRequest that = (DailyClassesRequest) other;
    return Objects.equals(clubId, that.clubId)
        && Objects.equals(date, that.date)
        && Objects.equals(categoryId, that.categoryId)
        && Objects.equals(timeTableId, that.timeTableId)
        && Objects.equals(trainerId, that.trainerId)
        && Objects.equals(zoneId, that.zoneId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clubId, date, categoryId, timeTableId, trainerId, zoneId);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
