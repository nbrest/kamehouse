package com.nicobrest.kamehouse.systemcommand.model;

/**
 * System command to press type content in the server screen using VncDo.
 * 
 * @author nbrest
 *
 */
public class VncDoTypeSystemCommand extends VncDoSystemCommand {

  public VncDoTypeSystemCommand(String content) {
    setVncDoSystemCommand("type", content);
  }
}
