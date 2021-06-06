package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.utils.FileUtils;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;

import java.util.Arrays;

/**
 * Base class for VncDo system commands.
 * 
 * @author nbrest
 *
 */
public abstract class VncDoSystemCommand extends SystemCommand {

  /**
   * Sets a VncDo system command that is specified by an action and a parameter.
   */
  protected void setVncDoSystemCommand(String action, String parameter) {
    String hostname = PropertiesUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    String vncDoCommandLinux = "/usr/local/bin/vncdo --server " + hostname + " --password "
        + vncServerPassword + " " + action + " " + parameter;
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", vncDoCommandLinux));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "vncdo", "--server", hostname,
        "--password", vncServerPassword, action, parameter));
    setOutputCommand();
  }

  /**
   * Gets the vnc server password from a file.
   */
  protected String getVncServerPassword() {
    String vncServerPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getAdminProperty("vnc.server.pwd.file");
    return FileUtils.getDecodedFileContent(vncServerPwdFile);
  }

  /**
   * Hide the output of vncdo commands, as it contains passwords.
   * Call this method in the constructor of <b>EVERY</b> concrete subclass,
   * after initializing the command lists.
   */
  @Override
  protected void setOutputCommand() {
    output.setCommand("[vncdo (hidden as it contains passwords)]");
  }

  @Override
  public String toString() {
    String[] maskedFields = { "linuxCommand", "windowsCommand" };
    return JsonUtils.toJsonString(this, super.toString(), maskedFields);
  }
}
