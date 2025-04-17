package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.util.List;

/**
 * KameHouse command to get the stats with full report of a running vlc player.
 *
 * @author nbrest
 */
public class VlcStatsFullReportKameHouseCommand extends KameHouseShellScript {

  private boolean updateStats;

  public VlcStatsFullReportKameHouseCommand(boolean updateStats) {
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
    return "common/vlc/vlc-stats-full-report.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    if (updateStats) {
      return null;
    }
    return List.of("--show-history-file-only");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "common/vlc/vlc-stats-full-report.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    if (updateStats) {
      return null;
    }
    return "--show-history-file-only";
  }
}
