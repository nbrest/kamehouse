package com.nicobrest.kamehouse.systemcommand.model;

import com.nicobrest.kamehouse.utils.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command to execute as a system process. It's a unique operation
 * executed through the command line.
 * 
 * @author nbrest
 *
 */
public abstract class SystemCommand {

  protected boolean isDaemon = false;
  protected List<String> linuxCommand = new ArrayList<>();
  protected List<String> windowsCommand = new ArrayList<>();

  public boolean isDaemon() {
    return isDaemon;
  }
  
  /**
   * Get the specified system command for the correct operating system.
   */
  public List<String> getCommand() {
    if (PropertiesUtils.isWindowsHost()) {
      return windowsCommand;
    } else {
      return linuxCommand;
    } 
  }
}
