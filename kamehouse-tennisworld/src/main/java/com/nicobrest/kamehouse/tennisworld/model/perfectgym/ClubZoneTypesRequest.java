package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Get club zone types request to tennisworld.
 *
 * @author nbrest
 */
public class ClubZoneTypesRequest implements RequestBody {

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
    ClubZoneTypesRequest that = (ClubZoneTypesRequest) other;
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
