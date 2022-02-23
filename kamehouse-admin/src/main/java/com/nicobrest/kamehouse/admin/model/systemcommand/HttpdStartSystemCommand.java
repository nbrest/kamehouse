package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import com.nicobrest.kamehouse.commons.utils.PropertiesUtils;

/**
 * System command to start the httpd server.
 *
 * @author nbrest
 */
public class HttpdStartSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public HttpdStartSystemCommand() {
    isDaemon = true;
    addBashPrefix();
    linuxCommand.add("sudo service apache2 start");
    addWindowsCmdStartPrefix();
    String userHome = PropertiesUtils.getUserHome();
    String httpdExecutableWin = PropertiesUtils.getProperty("httpd.executable.win");
    windowsCommand.add(userHome + httpdExecutableWin);
    setOutputCommand();
  }
}
