package com.nicobrest.kamehouse.testmodule.model;

import java.util.Date;

/**
 * Test WebSocket response message.
 * 
 * @author nbrest
 *
 */
public class TestWebSocketResponseMessage {

  private String message;
  private Date date = null;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getDate() {
    return (date != null ? (Date) date.clone() : null);
  }

  public void setDate(Date date) {
    this.date = (date != null ? (Date) date.clone() : null);
  }
}
