package com.nicobrest.kamehouse.commons.model.systemcommand;

import java.util.List;

/**
 * System command to click the mouse in the server screen using VncDo. The coordinates start from
 * "0" "0" on the top left of the screen.
 *
 * @author nbrest
 * @deprecated use {@link MouseClickJvncSenderSystemCommand}.
 */
@Deprecated(since = "v9.00")
public class VncDoMouseClickSystemCommand extends VncDoSystemCommand {

  private String numberOfClicks = null;
  private String horizontalPosition = null;
  private String verticalPosition = null;

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VncDoMouseClickSystemCommand(
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
