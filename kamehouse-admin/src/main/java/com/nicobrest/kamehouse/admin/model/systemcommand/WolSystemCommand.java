package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.KameHouseCmdSystemCommand;

/**
 * System command to send a wol package.
 *
 * @author nbrest
 */
public class WolSystemCommand extends KameHouseCmdSystemCommand {

  private String macAddress = null;
  private String broadcastAddress = null;

  public WolSystemCommand(String macAddress, String broadcastAddress) {
    this.macAddress = macAddress;
    this.broadcastAddress = broadcastAddress;
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getKameHouseCmdArguments() {
    return "-o wol -mac " + macAddress + " -broadcast " + broadcastAddress;
  }
}
