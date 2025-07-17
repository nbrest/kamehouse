package com.nicobrest.kamehouse.commons.model.kamehousecommand;

/**
 * Test Non Daemon command that should execute on the docker host to test the
 * KameHouseCommandService.
 *
 * @author nbrest
 */
public class TestNonDaemonCommand extends KameHouseShellScript {

  @Override
  public long getSshTimeout() {
    return 20000L;
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
    return "test-script.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "test-script.sh";
  }
}
