package com.nicobrest.kamehouse.commons.model;

import com.nicobrest.kamehouse.commons.model.systemcommand.VncDoKeyPressSystemCommand;

/**
 * Test Daemon command that should execute on the docker host to test the SystemCommandService.
 *
 * @author nbrest
 */
public class TestDaemonCommand extends VncDoKeyPressSystemCommand {

  /**
   * Test Daemon command.
   */
  public TestDaemonCommand(String key) {
    super(key);
    isDaemon = true;
    executeOnDockerHost = true;
  }
}
