package com.nicobrest.kamehouse.tennisworld.model;

/**
 * Tennis World site.
 */
public enum Site {
  ALBERT_RESERVE("Tennis World - Albert Reserve"),
  MELBOURNE_PARK("Tennis World - Melbourne Park");

  private String value;

  Site(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
