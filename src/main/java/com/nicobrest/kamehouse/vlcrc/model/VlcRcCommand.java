package com.nicobrest.kamehouse.vlcrc.model;

/**
 * Represents a command to be executed in a VLC Player.
 * 
 * @author nbrest
 *
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
}
