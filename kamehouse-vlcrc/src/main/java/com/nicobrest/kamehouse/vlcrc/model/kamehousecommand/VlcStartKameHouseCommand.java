package com.nicobrest.kamehouse.vlcrc.model.kamehousecommand;

import com.nicobrest.kamehouse.commons.model.kamehousecommand.KameHouseShellScript;
import com.nicobrest.kamehouse.commons.validator.InputValidator;
import java.util.List;

/**
 * KameHouse command to start a vlc player with an optional file (or playlist) to play.
 *
 * @author nbrest
 */
public class VlcStartKameHouseCommand extends KameHouseShellScript {

  private String filename = null;

  /**
   * Sets the command line for each operation required for this KameHouseCommand.
   */
  public VlcStartKameHouseCommand(String filename) {
    super();
    InputValidator.validateForbiddenCharsForShell(filename);
    this.filename = filename.replace("\\", "/");
  }

  @Override
  public long getSshTimeout() {
    return 20000L;
  }

  @Override
  public boolean hasSensitiveInformation() {
    return false;
  }

  @Override
  public boolean isDaemon() {
    return true;
  }

  @Override
  public boolean executeOnDockerHost() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "win/vlc/vlc-start.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    return List.of("-f", filename);
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "lin/vlc/vlc-start.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    return "-f " + filename;
  }
}
