package com.nicobrest.kamehouse.testmodule.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;

/**
 * Test WebSocket request message.
 *
 * @author nbrest
 */
public class TestWebSocketRequestMessage {

  private String firstName;
  private String lastName;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
