package com.nicobrest.kamehouse.tennisworld.model;

/**
 * Tennis World site.
 * <p>
 * <b>IMPORTANT:</b> This enum is mapped to columns in tables in the database on
 * {@link BookingRequest} and {@link BookingScheduleConfig} tables. So deleting or updating the
 * names of EXISTING enum entries will require database updates on those tables to map to the
 * updated enum names. Adding new enum entries doesn't require any manual database update. It will
 * also require updates in the booking-request.js, booking-response.js, booking-schedule-config.js
 * and one-off-bookings.html files to match the current state of the enum.
 * </p>
 *
 * @author nbrest
 */
public enum Site {
  ALBERT_RESERVE("Tennis World - Albert Reserve", "Tennis World Albert Reserve"),
  MELBOURNE_PARK("Tennis World - Melbourne Park", "Tennis World Melbourne");

  private String activeCarrotName;
  private String perfectGymName;

  Site(String activeCarrotName, String perfectGymName) {
    this.activeCarrotName = activeCarrotName;
    this.perfectGymName = perfectGymName;
  }

  public String getActiveCarrotName() {
    return activeCarrotName;
  }

  public String getPerfectGymName() {
    return perfectGymName;
  }
}
