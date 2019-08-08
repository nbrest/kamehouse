package com.nicobrest.kamehouse.testmodule.model;

import java.util.Date;

public class TestWebSocketResponseMessage {

    private String message;
    private Date date;

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }
}
