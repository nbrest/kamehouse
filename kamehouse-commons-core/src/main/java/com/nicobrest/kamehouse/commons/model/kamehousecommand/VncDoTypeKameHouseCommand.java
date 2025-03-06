package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import java.util.List;

/**
 * KameHouse command to press type content in the server screen using VncDo.
 *
 * @author nbrest
 * @deprecated use {@link JvncSenderKameHouseCommand}.
 */
@Deprecated(since = "v9.00")
public class VncDoTypeKameHouseCommand extends VncDoKameHouseCommand {

  private String content = null;

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public VncDoTypeKameHouseCommand(String content) {
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
