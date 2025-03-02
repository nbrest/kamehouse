package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.Arrays;
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
  protected List<String> buildLinuxCommand() {
    String hostname = DockerUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    addBashPrefix();
    String vncDoCommandLinux =
        "/usr/local/bin/vncdo --server "
            + hostname
            + " --password "
            + vncServerPassword
            + " "
            + getVncDoActionLinux();
    linuxCommand.add(vncDoCommandLinux);
    return linuxCommand;
  }

  @Override
  protected List<String> buildWindowsCommand() {
    String hostname = DockerUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    windowsCommand.addAll(
        Arrays.asList(
            "cmd.exe",
            "/c",
            "vncdo",
            "--server",
            hostname,
            "--password",
            vncServerPassword));
    windowsCommand.addAll(getVncDoActionWindows());
    return windowsCommand;
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
