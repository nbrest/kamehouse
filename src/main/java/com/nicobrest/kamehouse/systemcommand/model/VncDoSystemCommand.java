package com.nicobrest.kamehouse.systemcommand.model;

import com.nicobrest.kamehouse.utils.FileUtils;
import com.nicobrest.kamehouse.utils.PropertiesUtils;

import java.util.Arrays;

/**
 * Base class for VncDo system commands.
 * 
 * @author nbrest
 *
 */
public abstract class VncDoSystemCommand extends SystemCommand {

  /**
   * Set a VncDo system command that is specified by an action and a parameter.
   */
  protected void setVncDoSystemCommand(String action, String parameter) {
    String hostname = PropertiesUtils.getHostname();
    String vncServerPassword = getVncServerPassword();
    String vncDoCommandLinux = "/usr/local/bin/vncdo --server " + hostname + " --password "
        + vncServerPassword + " " + action + " " + parameter;
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", vncDoCommandLinux));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "vncdo", "--server", hostname,
        "--password", vncServerPassword, action));
    windowsCommand.add(parameter);
  }

  /**
   * Get the vnc server password from a file.
   */
  protected String getVncServerPassword() {
    String vncServerPwdFile = PropertiesUtils.getUserHome() + "/" + PropertiesUtils
        .getAdminProperty("vnc.server.pwd.file");
    return FileUtils.getDecodedFileContent(vncServerPwdFile);
  }
}
