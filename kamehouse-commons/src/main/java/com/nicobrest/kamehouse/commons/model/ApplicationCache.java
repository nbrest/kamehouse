package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.annotations.Masked;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Cache representation object used to display cache information on the frontend.
 *
 * @author nbrest
 */
public class ApplicationCache {

  private String name;
  private String status;
  private String keys;
  @Masked
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

  @SuppressFBWarnings(value = "EI_EXPOSE_REP")
  public List<String> getValues() {
    return values;
  }

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
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
      return new EqualsBuilder()
          .append(name, other.getName())
          .append(status, other.getStatus())
          .append(keys, other.getKeys())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
