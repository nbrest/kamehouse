package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import java.util.List;

/**
 * KameHouse command to click the mouse in the server screen using VncDo. The coordinates start from
 * "0" "0" on the top left of the screen.
 *
 * @author nbrest
 * @deprecated use {@link MouseClickJvncSenderKameHouseCommand}.
 */
@Deprecated(since = "v9.00")
public class VncDoMouseClickKameHouseCommand extends VncDoKameHouseCommand {

  private String numberOfClicks = null;
  private String horizontalPosition = null;
  private String verticalPosition = null;

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public VncDoMouseClickKameHouseCommand(
      String numberOfClicks, String horizontalPosition, String verticalPosition) {
    super();
    this.numberOfClicks = numberOfClicks;
    this.horizontalPosition = horizontalPosition;
    this.verticalPosition = verticalPosition;
  }

  @Override
  protected String getVncDoActionLinux() {
    return "move " + horizontalPosition + " " + verticalPosition + " click " + numberOfClicks;
  }

  @Override
  protected List<String> getVncDoActionWindows() {
    return List.of("move", horizontalPosition, verticalPosition, "click", numberOfClicks);
  }
}
