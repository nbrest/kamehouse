package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

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

    String httpdExecutable = "C:\\Users\\nbrest\\programs\\apache-httpd\\bin\\httpd.exe";
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "sudo service apache2 start"));
    windowsCommand.addAll(Arrays.asList("cmd.exe", "/c", "start", httpdExecutable));
    setOutputCommand();
  }
}
