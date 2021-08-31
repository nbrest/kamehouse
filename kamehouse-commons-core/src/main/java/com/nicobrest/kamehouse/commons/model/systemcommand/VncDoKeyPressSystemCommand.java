package com.nicobrest.kamehouse.commons.model.systemcommand;

/**
 * System command to press a key in the server screen using VncDo.
 *
 * @author nbrest
 */
public class VncDoKeyPressSystemCommand extends VncDoSystemCommand {

  /** Sets the command line for each operation system required for this SystemCommand. */
  public VncDoKeyPressSystemCommand(String key) {
    setVncDoSystemCommand("key", key);
    setOutputCommand();
  }
}
