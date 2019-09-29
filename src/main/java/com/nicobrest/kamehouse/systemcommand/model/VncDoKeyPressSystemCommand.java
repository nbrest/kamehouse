package com.nicobrest.kamehouse.systemcommand.model;

/**
 * System command to press a key in the server screen using VncDo.
 * 
 * @author nbrest
 *
 */
public class VncDoKeyPressSystemCommand extends VncDoSystemCommand {

  public VncDoKeyPressSystemCommand(String key) {
    setVncDoSystemCommand("key", key);
  }
}
