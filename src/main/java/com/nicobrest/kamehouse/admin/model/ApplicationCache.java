package com.nicobrest.kamehouse.admin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Cache representation object used to display cache information on the
 * frontend.
 * 
 * @author nbrest
 *
 */
public class ApplicationCache {

  private String name;
  private String status;
  private String keys;
  private List<String> values;

  /**
   * Default constructor.
   */
  public ApplicationCache() {
    values = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getKeys() {
    return keys;
  }

  public void setKeys(String keys) {
    this.keys = keys;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }
}
