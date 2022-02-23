package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to stop the httpd server.
 *
 * @author nbrest
 */
public class HttpdStopSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public HttpdStopSystemCommand() {
    sleepTime = 7;
    addBashPrefix();
    linuxCommand.add("sudo service apache2 stop ; echo Stopping apache httpd");
    addWindowsCmdStartPrefix();
    windowsCommand.addAll(Arrays.asList(
        "taskkill", "/im", "httpd.exe", "&", "echo", "Stopping apache httpd"));
    setOutputCommand();
  }
}
