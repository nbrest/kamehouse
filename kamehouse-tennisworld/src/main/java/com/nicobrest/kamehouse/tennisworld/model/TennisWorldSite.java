package com.nicobrest.kamehouse.tennisworld.model;

public enum TennisWorldSite {
  MELBOURNE_PARK("Tennis World - Melbourne Park")
  ;

  private String value;

  TennisWorldSite(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
