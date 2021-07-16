package com.nicobrest.kamehouse.tennisworld.model;

public enum TennisWorldSessionType {
  CARDIO("Book a Cardio Tennis Class"),
  NTC_CLAY_COURTS("NTC Clay Courts"),
  NTC_OUTDOOR("National Tennis Outdoor"),
  ROD_LAVER_OUTDOOR("Rod Laver Arena Outdoor"),
  ROD_LAVER_SHOW_COURTS("Rod Laver Arena Show Courts"),
  UNKNOWN("Unknown session type")
  ;

  private String value;

  TennisWorldSessionType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
