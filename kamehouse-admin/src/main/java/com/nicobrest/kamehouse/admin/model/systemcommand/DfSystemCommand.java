package com.nicobrest.kamehouse.admin.model.systemcommand;

import com.nicobrest.kamehouse.commons.model.systemcommand.SystemCommand;
import java.util.Arrays;

/**
 * System command to get the available disk space of the server.
 *
 * @author nbrest
 */
public class DfSystemCommand extends SystemCommand {

  /** Sets the command line for each operation system required for this SystemCommand. */
  public DfSystemCommand() {
    linuxCommand.addAll(Arrays.asList("/bin/bash", "-c", "df -h"));
    windowsCommand.addAll(Arrays.asList("powershell.exe", "-c", "gdr"));
    setOutputCommand();
  }
}
