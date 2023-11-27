package com.nicobrest.kamehouse.commons.model;

/**
 * SystemCommand status.
 */
public enum SystemCommandStatus {
  COMPLETED("completed"),
  FAILED("failed"),
  RUNNING("running")
  ;

  private String status;

  SystemCommandStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}
