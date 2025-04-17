package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.util.List;

/**
 * KameHouse command to get the stats of a running vlc player.
 *
 * @author nbrest
 */
public class VlcStatsKameHouseCommand extends KameHouseShellScript {

  private boolean updateStats;

  public VlcStatsKameHouseCommand(boolean updateStats) {
    this.updateStats = updateStats;
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
    return "common/vlc/vlc-stats.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    if (updateStats) {
      return List.of("-n", "2");
    }
    return List.of("-n", "2", "--show-history-file-only");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "common/vlc/vlc-stats.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    if (updateStats) {
      return "-n 8";
    }
    return "-n 8 --show-history-file-only";
  }
}
