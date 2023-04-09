package com.nicobrest.kamehouse.tennisworld.model.perfectgym;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.Objects;

/**
 * Finalize court booking request to tennisworld.
 *
 * @author nbrest
 */
public class FinalizeCourtBookingRequest implements RequestBody {

  private long ruleId;

  @JsonProperty("OtherCalendarEventBookedAtRequestedTime")
  private boolean otherCalendarEventBookedAtRequestedTime;

  @JsonProperty("HasUserRequiredProducts")
  private boolean hasUserRequiredProducts;

  public long getRuleId() {
    return ruleId;
  }

  public void setRuleId(long ruleId) {
    this.ruleId = ruleId;
  }

  public boolean isOtherCalendarEventBookedAtRequestedTime() {
    return otherCalendarEventBookedAtRequestedTime;
  }

  public void setOtherCalendarEventBookedAtRequestedTime(
      boolean otherCalendarEventBookedAtRequestedTime) {
    this.otherCalendarEventBookedAtRequestedTime = otherCalendarEventBookedAtRequestedTime;
  }

  public boolean isHasUserRequiredProducts() {
    return hasUserRequiredProducts;
  }

  public void setHasUserRequiredProducts(boolean hasUserRequiredProducts) {
    this.hasUserRequiredProducts = hasUserRequiredProducts;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    FinalizeCourtBookingRequest that = (FinalizeCourtBookingRequest) other;
    return Objects.equals(ruleId, that.ruleId)
        && Objects.equals(otherCalendarEventBookedAtRequestedTime,
        that.otherCalendarEventBookedAtRequestedTime)
        && Objects.equals(hasUserRequiredProducts, that.hasUserRequiredProducts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ruleId, otherCalendarEventBookedAtRequestedTime, hasUserRequiredProducts);
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
