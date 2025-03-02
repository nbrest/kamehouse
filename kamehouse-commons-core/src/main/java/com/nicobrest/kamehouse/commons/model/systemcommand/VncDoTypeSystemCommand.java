package com.nicobrest.kamehouse.commons.model.systemcommand;

import java.util.List;

/**
 * System command to press type content in the server screen using VncDo.
 *
 * @author nbrest
 * @deprecated use {@link JvncSenderSystemCommand}.
 */
@Deprecated(since = "v9.00")
public class VncDoTypeSystemCommand extends VncDoSystemCommand {

  private String content = null;

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VncDoTypeSystemCommand(String content) {
    super();
    this.content = content;
  }

  @Override
  protected String getVncDoActionLinux() {
    return "type " + content;
  }

  @Override
  protected List<String> getVncDoActionWindows() {
    return List.of("type", content);
  }
}
