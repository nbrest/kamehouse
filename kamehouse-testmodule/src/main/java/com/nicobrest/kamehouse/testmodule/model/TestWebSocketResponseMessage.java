package com.nicobrest.kamehouse.testmodule.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.time.LocalDateTime;

/**
 * Test WebSocket response message.
 *
 * @author nbrest
 */
public class TestWebSocketResponseMessage {

  private String message;
  private LocalDateTime date = null;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
