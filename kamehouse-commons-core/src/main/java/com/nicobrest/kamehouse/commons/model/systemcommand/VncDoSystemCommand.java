package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.List;

/**
 * Base class for VncDo system commands to send text and mouse clicks to a VNC server.
 *
 * @author nbrest
 * @deprecated use {@link JvncSenderSystemCommand}.
 */
@Deprecated(since = "v9.00")
public abstract class VncDoSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation required for this SystemCommand.
   */
  public VncDoSystemCommand() {
    logCommand = false;
    executeOnDockerHost = true;
  }

  /**
   * Return the action to execute through vncdo.
   */
  protected abstract String getVncDoActionLinux();

  /**
   * Return the action list to execute through vncdo.
   */
  protected abstract List<String> getVncDoActionWindows();

  @Override
  protected boolean hideOutputCommand() {
    return true;
  }

  @Override
  protected String getWindowsKameHouseShellScript() {
    return "kamehouse/vncdo.sh";
  }

  @Override
  protected List<String> getWindowsKameHouseShellScriptArguments() {
    String hostname = DockerUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    List<String> args = List.of("--server", hostname, "--password", vncServerPassword);
    args.addAll(getVncDoActionWindows());
    return args;
  }

  @Override
  protected String getLinuxKameHouseShellScript() {
    return "kamehouse/vncdo.sh";
  }

  @Override
  protected String getLinuxKameHouseShellScriptArguments() {
    String hostname = DockerUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    return "--server " + hostname + " --password " + vncServerPassword + " "
        + getVncDoActionLinux();
  }

  /**
   * Gets the vnc server password from a file.
   */
  protected String getVncServerPassword() {
    String vncServerPwdFile =
        PropertiesUtils.getUserHome() + "/" + PropertiesUtils.getProperty("vnc.server.pwd.file");
    try {
      String decryptedFile = EncryptionUtils.decryptKameHouseFileToString(vncServerPwdFile);
      if (StringUtils.isEmpty(decryptedFile)) {
        decryptedFile = FileUtils.EMPTY_FILE_CONTENT;
      }
      return decryptedFile;
    } catch (KameHouseInvalidDataException e) {
      return FileUtils.EMPTY_FILE_CONTENT;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
