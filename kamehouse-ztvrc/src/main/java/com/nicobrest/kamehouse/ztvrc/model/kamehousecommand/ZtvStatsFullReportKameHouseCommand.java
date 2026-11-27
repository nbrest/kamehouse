package com.nicobrest.kamehouse.ztvrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import java.util.Collections;
import java.util.List;

/**
 * KameHouse command to get the stats with full report of a running ZTV Player.
 *
 * @author nbrest
 */
public class ZtvStatsFullReportKameHouseCommand extends KameHouseShellScript {

  private final boolean updateStats;

  public ZtvStatsFullReportKameHouseCommand(boolean updateStats) {
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
    return "ztv/ztv-stats-full-report.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    if (updateStats) {
      return Collections.emptyList();
    }
    return List.of("--show-history-file-only");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "ztv/ztv-stats-full-report.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    if (updateStats) {
      return null;
    }
    return "--show-history-file-only";
  }
}
