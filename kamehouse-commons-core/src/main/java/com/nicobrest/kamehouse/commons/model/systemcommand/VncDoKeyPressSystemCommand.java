package com.nicobrest.kamehouse.commons.model.systemcommand;

import java.util.List;

/**
 * System command to press a key in the server screen using VncDo.
 *
 * @author nbrest
 * @deprecated use {@link TextJvncSenderSystemCommand}.
 */
@Deprecated(since = "v9.00")
public class VncDoKeyPressSystemCommand extends VncDoSystemCommand {

  private String key = null;

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VncDoKeyPressSystemCommand(String key) {
    super();
    this.key = key;
  }

  @Override
  protected String getVncDoActionLinux() {
    return "key " + key;
  }

  @Override
  protected List<String> getVncDoActionWindows() {
    return List.of("key", key);
  }
}
