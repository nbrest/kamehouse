package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseShellSystemCommand;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import java.util.List;

/**
 * System command to lock the screen.
 *
 * @author nbrest
 */
public class ScreenLockSystemCommand extends KameHouseShellSystemCommand {

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/screen/screen-lock.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    if (!DockerUtils.shouldExecuteOnDockerHost(executeOnDockerHost)) {
      return null;
    }
    // when executing remotely from docker, run as a scheduled task
    return List.of("--use-scheduled-task");
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/screen/screen-lock.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return null;
  }
}
