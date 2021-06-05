package com.nicobrest.kamehouse.admin.model;

import com.nicobrest.kamehouse.main.utils.JsonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
  private List<String> values = new ArrayList<>();

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

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(name).append(status).append(keys).toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ApplicationCache) {
      final ApplicationCache other = (ApplicationCache) obj;
      return new EqualsBuilder().append(name, other.getName()).append(status, other.getStatus())
          .append(keys, other.getKeys()).isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    String[] maskedFields = { "values" };
    return JsonUtils.toJsonString(this, super.toString(), maskedFields);
  }
}
