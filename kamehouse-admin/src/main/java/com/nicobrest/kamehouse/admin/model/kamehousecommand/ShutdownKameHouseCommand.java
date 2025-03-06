package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidCommandException;
import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.util.List;

/**
 * KameHouse command to shut down the server.
 *
 * @author nbrest
 */
public class ShutdownKameHouseCommand extends KameHouseShellScript {

  private int shutdownDelaySeconds = -1;

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public ShutdownKameHouseCommand(int shutdownDelaySeconds) {
    super();
    if (shutdownDelaySeconds < 0) {
      throw new KameHouseInvalidCommandException(
          "Invalid time for shutdown command " + shutdownDelaySeconds);
    }
    this.shutdownDelaySeconds = shutdownDelaySeconds;
  }

  @Override
  public boolean hasSensitiveInformation() {
    return false;
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/shutdown/shutdown.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return List.of("-s", "-t", String.valueOf(shutdownDelaySeconds));
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/shutdown/shutdown.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    int shutdownDelayMinutes = 0;
    if (shutdownDelaySeconds >= 60) {
      shutdownDelayMinutes = shutdownDelaySeconds / 60;
    }
    return "-d " + shutdownDelayMinutes;
  }
}
