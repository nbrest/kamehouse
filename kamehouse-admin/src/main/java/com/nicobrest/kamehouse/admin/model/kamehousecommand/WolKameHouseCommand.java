package com.nicobrest.kamehouse.admin.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseCmdKameHouseCommand;

/**
 * KameHouse command to send a wol package.
 *
 * @author nbrest
 */
public class WolKameHouseCommand extends KameHouseCmdKameHouseCommand {

  private String macAddress = null;
  private String broadcastAddress = null;

  public WolKameHouseCommand(String macAddress, String broadcastAddress) {
    this.macAddress = macAddress;
    this.broadcastAddress = broadcastAddress;
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
  protected String getKameHouseCmdArguments() {
    return "-o wol -mac " + macAddress + " -broadcast " + broadcastAddress;
  }
}
