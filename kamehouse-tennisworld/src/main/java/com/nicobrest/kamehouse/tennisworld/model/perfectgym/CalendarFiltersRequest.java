package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Get calendar filters request to tennisworld.
 *
 * @author nbrest
 */
public class CalendarFiltersRequest implements RequestBody {

  private long clubId;

  public long getClubId() {
    return clubId;
  }

  public void setClubId(long clubId) {
    this.clubId = clubId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    CalendarFiltersRequest that = (CalendarFiltersRequest) other;
    return Objects.equals(clubId, that.clubId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clubId);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
