package com.nicobrest.kamehouse.commons.model;

/**
 * KameHouseCommand status.
 */
public enum KameHouseCommandStatus {
  COMPLETED("completed"),
  FAILED("failed"),
  RUNNING("running")
  ;

  private String status;

  KameHouseCommandStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}
