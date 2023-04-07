package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Get calendar filters request to tennisworld.
 *
 * @author nbrest
 */
public class BookConfirmationRequest implements RequestBody {

  private long clubId;
  private long classId;

  public long getClubId() {
    return clubId;
  }

  public void setClubId(long clubId) {
    this.clubId = clubId;
  }

  public long getClassId() {
    return classId;
  }

  public void setClassId(long classId) {
    this.classId = classId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    BookConfirmationRequest that = (BookConfirmationRequest) other;
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
