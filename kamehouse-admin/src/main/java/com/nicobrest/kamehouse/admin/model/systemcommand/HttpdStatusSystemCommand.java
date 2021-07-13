package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;

import java.util.Arrays;

/**
 * System command to get the status of the httpd server.
 * 
 * @author nbrest
 *
 */
public class HttpdStatusSystemCommand extends SystemCommand {

  /**
   * Sets the command line for each operation system required for this SystemCommand.
   */
  public HttpdStatusSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c",
        "sudo netstat -nltp | grep 80 | grep apache"));
    windowsCommand.addAll(Arrays.asList("tasklist", "/FI", "IMAGENAME eq httpd.exe"));
    setOutputCommand();
  }
}
