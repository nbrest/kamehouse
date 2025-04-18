package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import java.util.List;

/**
 * KameHouse command to get a kamehouse secret from the secrets store.
 *
 * @author nbrest
 */
public class GetKameHouseSecretKameHouseCommand extends KameHouseShellScript {

  private String secretKey;

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public GetKameHouseSecretKameHouseCommand(String secretKey) {
    this.secretKey = secretKey;
  }

  @Override
  public boolean hasSensitiveInformation() {
    return false;
  }

  @Override
  public boolean useGitBashSilent() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/get-kamehouse-secret.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return List.of("-s", secretKey);
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/get-kamehouse-secret.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return "-s " + secretKey;
  }
}
