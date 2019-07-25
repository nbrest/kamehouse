package com.nicobrest.kamehouse.admin.model;

/**
 * Represents Admin Commands in the application.
 * These are commands that are usually translated to system commands executed
 * through the command line.
 * 
 * @author nbrest
 *
 */
public class AdminCommand {

  public static final String SCREEN_LOCK = "screen_lock";
  public static final String SCREEN_UNLOCK = "screen_unlock";
  public static final String SCREEN_WAKE_UP = "screen_wake_up";
  public static final String SHUTDOWN_CANCEL = "shutdown_cancel";
  public static final String SHUTDOWN_SET = "shutdown_set";
  public static final String SHUTDOWN_STATUS = "shutdown_status";
  public static final String VLC_START = "vlc_start";
  public static final String VLC_STATUS = "vlc_status";
  public static final String VLC_STOP = "vlc_stop";

  private String command;
  private String file;
  // Time in seconds (delay to shutdown)
  private int time;

  public AdminCommand() {
  }

  public AdminCommand(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }
}
