package com.nicobrest.kamehouse.commons.model.systemcommand;

import com.nicobrest.kamehouse.commons.exception.KameHouseInvalidDataException;
import com.nicobrest.kamehouse.commons.utils.DockerUtils;
import com.nicobrest.kamehouse.commons.utils.EncryptionUtils;
import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;
import com.nicobrest.kamehouse.commons.utils.StringUtils;
import java.util.Arrays;

/**
 * Base class for VncDo system commands.
 *
 * @author nbrest
 */
public abstract class VncDoSystemCommand extends SystemCommand {

  /**
   * Sets a VncDo system command that is specified by an action and a parameter.
   */
  protected void setVncDoSystemCommand(String action, String parameter) {
    logCommand = false;
    executeOnDockerHost = true;
    String hostname = DockerUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    addBashPrefix();
    String vncDoCommandLinux =
        "/usr/local/bin/vncdo --server "
            + hostname
            + " --password "
            + vncServerPassword
            + " "
            + action
            + " "
            + parameter;
    linuxCommand.add(vncDoCommandLinux);
    windowsCommand.addAll(
        Arrays.asList(
            "cmd.exe",
            "/c",
            "vncdo",
            "--server",
            hostname,
            "--password",
            vncServerPassword,
            action,
            parameter));
    setOutputCommand();
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

  /**
   * Hide the output of vncdo commands, as it contains passwords. Call this method in the
   * constructor of <b>EVERY</b> concrete subclass, after initializing the command lists.
   */
  @Override
  protected void setOutputCommand() {
    output.setCommand("[vncdo (hidden as it contains passwords)]");
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
