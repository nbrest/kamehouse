package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;

import java.util.Arrays;

/**
 * System command to start the httpd server.
 * 
 * @author nbrest
 *
 */
public class HttpdStartSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public HttpdStartSystemCommand() {
    isDaemon = true;
    String userHome = PropertiesUtils.getUserHome();
    String httpdExecutableWin = PropertiesUtils.getProperty("httpd.executable.win");
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "sudo service apache2 start"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", userHome + httpdExecutableWin));
    setOutputCommand();
  }
}
