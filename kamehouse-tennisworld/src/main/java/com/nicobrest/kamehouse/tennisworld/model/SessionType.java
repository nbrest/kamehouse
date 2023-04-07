package com.nicobrest.kamehouse.tennisworld.model;

/**
 * TennisWorld session type.
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
public enum SessionType {
  ADULT_MATCH_PLAY_SINGLES("N/A",
      "TW Adult Match Play - SINGLES"),
  ADULT_MATCH_PLAY_DOUBLES("N/A",
      "TW Adult Match Play - DOUBLES"),
  ADULT_SOCIAL_PLAY("N/A",
      "Adult Social Play"),
  CARDIO("Book a Cardio Tennis Class",
      "Cardio Tennis/Cardio Play"),
  CARDIO_ACTIV8("N/A",
      "Cardio Activ8"),
  NTC_CLAY_COURTS("NTC Clay Courts",
      "TWMP National Tennis Centre Clay Courts"),
  NTC_INDOOR("N/A",
      "TWMP National Tennis Centre Indoor Courts"),
  NTC_OUTDOOR("National Tennis Outdoor",
      "TWMP National Tennis Centre Outdoor Courts"),
  ROD_LAVER_OUTDOOR_EASTERN("Rod Laver Arena Outdoor",
      "TWMP Eastern Courts -  (Full Court)"),
  ROD_LAVER_OUTDOOR_WESTERN("Rod Laver Arena Outdoor",
      "TWMP Western Courts -  (Outdoor Full Court)"),
  ROD_LAVER_SHOW_COURTS("Rod Laver Arena Show Courts",
      "TWMP Western Courts -  (Outdoor Show Court)"),
  UNKNOWN("Unknown session type",
      "Unknown session type");

  private String activeCarrotName;
  private String perfectGymName;

  SessionType(String activeCarrotName, String perfectGymName) {
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
