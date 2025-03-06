package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import java.util.List;

/**
 * KameHouse command to press a key in the server screen using VncDo.
 *
 * @author nbrest
 * @deprecated use {@link TextJvncSenderKameHouseCommand}.
 */
@Deprecated(since = "v9.00")
public class VncDoKeyPressKameHouseCommand extends VncDoKameHouseCommand {

  private String key = null;

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public VncDoKeyPressKameHouseCommand(String key) {
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
