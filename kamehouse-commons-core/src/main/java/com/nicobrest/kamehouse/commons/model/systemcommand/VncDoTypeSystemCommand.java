package com.nicobrest.kamehouse.commons.model.systemcommand;

/**
 * System command to press type content in the server screen using VncDo.
 *
 * @author nbrest
 * @deprecated use {@link JvncSenderSystemCommand}.
 */
@Deprecated(since = "v9.00")
public class VncDoTypeSystemCommand extends VncDoSystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VncDoTypeSystemCommand(String content) {
    setVncDoSystemCommand("type", content);
    setOutputCommand();
  }
}
