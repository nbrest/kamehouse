package com.nicobrest.kamehouse.vlcrc.model;

import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a command to be executed in a VLC Player.
 *
 * @author nbrest
 */
public class VlcRcCommand {

  private String name;
  private String input;
  private String option;
  private String val;
  private String id;
  private String band;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setInput(String input) {
    this.input = input;
  }

  public String getInput() {
    return input;
  }

  public void setOption(String option) {
    this.option = option;
  }

  public String getOption() {
    return option;
  }

  public void setVal(String val) {
    this.val = val;
  }

  public String getVal() {
    return val;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setBand(String band) {
    this.band = band;
  }

  public String getBand() {
    return band;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(id)
        .append(input)
        .append(option)
        .append(val)
        .append(id)
        .append(band)
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof VlcRcCommand) {
      final VlcRcCommand other = (VlcRcCommand) obj;
      return new EqualsBuilder()
          .append(id, other.getId())
          .append(input, other.getInput())
          .append(option, other.getOption())
          .append(val, other.getVal())
          .append(id, other.getId())
          .append(band, other.getBand())
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
