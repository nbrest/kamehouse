package com.nicobrest.kamehouse.admin.model.systemcommand;

/**
 * System command to press type content in the server screen using VncDo.
 * 
 * @author nbrest
 *
 */
public class VncDoTypeSystemCommand extends VncDoSystemCommand {

  /**
   * Set the command line for each operation system required for this SystemCommand.
   */
  public VncDoTypeSystemCommand(String content) {
    setVncDoSystemCommand("type", content);
    setOutputCommand();
  }
}
